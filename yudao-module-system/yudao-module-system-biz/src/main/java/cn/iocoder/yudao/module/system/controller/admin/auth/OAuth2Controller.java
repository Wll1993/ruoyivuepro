package cn.iocoder.yudao.module.system.controller.admin.auth;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.enums.auth.OAuth2GrantTypeEnum;
import cn.iocoder.yudao.module.system.service.auth.OAuth2ApproveService;
import cn.iocoder.yudao.module.system.service.auth.OAuth2ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Api(tags = "管理后台 - OAuth2.0 授权")
@RestController
@RequestMapping("/system/oauth2")
@Validated
@Slf4j
public class OAuth2Controller {

//    POST oauth/token TokenEndpoint：Password、Implicit、Code、Refresh Token

//    POST oauth/check_token CheckTokenEndpoint

//    DELETE oauth/token ConsumerTokenServices#revokeToken

//    GET  oauth/authorize AuthorizationEndpoint

    @Resource
    private OAuth2ClientService oauth2ClientService;
    @Resource
    private OAuth2ApproveService oauth2ApproveService;

    @GetMapping("/authorize")
    @ApiOperation(value = "获得授权信息", notes = "适合 code 授权码模式，或者 implicit 简化模式；在 authorize.vue 单点登录界面被【获取】调用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "response_type", required = true, value = "响应类型", example = "code", dataTypeClass = String.class),
            @ApiImplicitParam(name = "client_id", required = true, value = "客户端编号", example = "tudou", dataTypeClass = String.class),
            @ApiImplicitParam(name = "scope", value = "授权范围", example = "userinfo.read", dataTypeClass = String.class), // 多个使用空格分隔
            @ApiImplicitParam(name = "redirect_uri", required = true, value = "重定向 URI", example = "https://www.iocoder.cn", dataTypeClass = String.class),
            @ApiImplicitParam(name = "state", example = "123321", dataTypeClass = String.class)
    })
    // 情况一：满足自动授权，则走类似 approveOrDeny 的逻辑，最终返回是 CommonResult<String>
    // 情况二：如果不满足自动授权，则返回授权相关的展示信息，最终返回是 CommonResult<OAuth2AuthorizeRespInfo>
    public CommonResult<Object> authorize(@RequestParam("response_type") String responseType,
                                          @RequestParam("client_id") String clientId,
                                          @RequestParam(value = "scope", required = false) String scope,
                                          @RequestParam("redirect_uri") String redirectUri,
                                          @RequestParam(value = "state", required = false) String state) {
        List<String> scopes = StrUtil.split(scope, ' ');
        // 0. 校验用户已经登录。通过 Spring Security 实现

        // 1.1 校验 responseType 是否满足 code 或者 token 值
        OAuth2GrantTypeEnum grantTypeEnum = getGrantTypeEnum(responseType);
        // 1.2 校验 redirectUri 重定向域名是否合法 + 校验 scope 是否在 Client 授权范围内
        oauth2ClientService.validOAuthClientFromCache(clientId, grantTypeEnum.getGrantType(), scopes, redirectUri);

        // 2. 判断是否满足自动授权（满足)
        boolean approved = oauth2ApproveService.checkForPreApproval(getLoginUserId(), UserTypeEnum.ADMIN.getValue(), clientId, scopes);
        if (approved) {
            // 2.1 如果是 code 授权码模式，则发放 code 授权码，并重定向
            if (grantTypeEnum == OAuth2GrantTypeEnum.AUTHORIZATION_CODE) {
                return success(getAuthorizationCodeRedirect());
            }
            return success(getImplicitGrantRedirect());
        }

        // 3. 不满足自动授权，则返回授权相关的展示信息
        return null;
    }

    @PostMapping("/authorize")
    @ApiOperation(value = "申请授权", notes = "适合 code 授权码模式，或者 implicit 简化模式；在 authorize.vue 单点登录界面被【提交】调用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "response_type", required = true, value = "响应类型", example = "code", dataTypeClass = String.class),
            @ApiImplicitParam(name = "client_id", required = true, value = "客户端编号", example = "tudou", dataTypeClass = String.class),
            @ApiImplicitParam(name = "scope", value = "授权范围", example = "userinfo.read", dataTypeClass = String.class), // 使用 Map<String, Boolean> 格式，Spring MVC 暂时不支持这么接收参数
            @ApiImplicitParam(name = "redirect_uri", required = true, value = "重定向 URI", example = "https://www.iocoder.cn", dataTypeClass = String.class),
            @ApiImplicitParam(name = "approved", value = "用户是否接受", example = "true", dataTypeClass = Boolean.class), // 该参数为 null 时，会基于用户是否已经授权过，进行自动判断
            @ApiImplicitParam(name = "state", example = "123321", dataTypeClass = String.class)
    })
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    // 因为前后端分离，Axios 无法很好的处理 302 重定向，所以和 Spring Security OAuth 略有不同，返回结果是重定向的 URL，剩余交给前端处理
    public CommonResult<String> approveOrDeny(@RequestParam("response_type") String responseType,
                                              @RequestParam("client_id") String clientId,
                                              @RequestParam(value = "scope", required = false) String scope,
                                              @RequestParam("redirect_uri") String redirectUri,
                                              @RequestParam("approved") Boolean approved,
                                              @RequestParam(value = "state", required = false) String state) {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> scopes = JsonUtils.parseObject(scope, Map.class);
        scopes = ObjectUtil.defaultIfNull(scopes, Collections.emptyMap());
        // TODO 芋艿：针对 approved + scopes 在看看 spring security 的实现
        // 0. 校验用户已经登录。通过 Spring Security 实现

        // 1.1 校验 responseType 是否满足 code 或者 token 值
        OAuth2GrantTypeEnum grantTypeEnum = getGrantTypeEnum(responseType);
        // 1.2 校验 redirectUri 重定向域名是否合法 + 校验 scope 是否在 Client 授权范围内
        oauth2ClientService.validOAuthClientFromCache(clientId, grantTypeEnum.getGrantType(), scopes.keySet(), redirectUri);

        // 2.1
        // 2.2

        // 3.1 如果是 code 授权码模式，则发放 code 授权码，并重定向
        if (grantTypeEnum == OAuth2GrantTypeEnum.AUTHORIZATION_CODE) {
            return success(getAuthorizationCodeRedirect());
        }
        // 3.2 如果是 token 则是 implicit 简化模式，则发送 accessToken 访问令牌，并重定向
        return success(getImplicitGrantRedirect());
    }

    private static OAuth2GrantTypeEnum getGrantTypeEnum(String responseType) {
        if (StrUtil.equals(responseType, "code")) {
            return OAuth2GrantTypeEnum.AUTHORIZATION_CODE;
        }
        if (StrUtil.equalsAny(responseType, "token")) {
            return OAuth2GrantTypeEnum.IMPLICIT;
        }
        throw exception0(BAD_REQUEST.getCode(), "response_type 参数值允许 code 和 token");
    }

    private String getImplicitGrantRedirect() {
        return "";
    }

    private String getAuthorizationCodeRedirect() {
        return "";
    }

}
