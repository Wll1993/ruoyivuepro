package cn.iocoder.yudao.module.system.controller.admin.mail.vo.account;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ApiModel("管理后台 - 邮箱账号分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailAccountPageReqVO extends PageParam {

    @ApiModelProperty(value = "邮箱" , required = true , example = "yudaoyuanma@123.com")
    private String fromAddress;

    @ApiModelProperty(value = "用户名" , required = true , example = "yudao")
    private String username;

    @ApiModelProperty(value = "密码" , required = true , example = "123456")
    private String password;

    @ApiModelProperty(value = "网站" , required = true , example = "www.iocoder.cn")
    private String host;

    @ApiModelProperty(value = "端口" , required = true , example = "80")
    private String port;

    @ApiModelProperty(value = "是否开启ssl" , required = true , example = "2")
    private Boolean sslEnable;
}
