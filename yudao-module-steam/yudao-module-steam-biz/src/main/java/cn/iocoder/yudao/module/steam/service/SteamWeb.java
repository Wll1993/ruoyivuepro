package cn.iocoder.yudao.module.steam.service;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.steam.dal.dataobject.binduser.BindUserDO;
import cn.iocoder.yudao.module.steam.service.steam.*;
import cn.iocoder.yudao.module.steam.utils.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * steam机器人
 * 注意此类为非线程安全，因此不能作为spring bean直接注入，建议直接new相关接口
 * @author glzaboy
 */
@Slf4j
public class SteamWeb {
    /**
     * 登录后cookie信息
     */
    @Getter
    private String cookieString = "";
    /**
     * webapikey
     */
    @Getter
    private Optional<String> webApiKey = Optional.empty();
    /**
     * steamId
     */
    @Getter
    private Optional<String> steamId = Optional.empty();
    /**
     * sessionId
     * 会话ID
     */
    @Getter
    private Optional<String> sessionId = Optional.empty();
    /**
     * 交易链接
     */
    @Getter
    private Optional<String> treadUrl = Optional.empty();
    /**
     * steam用户名
     */
    @Getter
    private Optional<String> steamName = Optional.empty();
    /**
     * steam用户名头像
     */
    @Getter
    private Optional<String> steamAvatar = Optional.empty();


    private ConfigService configService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Optional<String> browserid;

    private SteamMaFile steamMaFile;

    public SteamWeb(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 如果检测是否需要登录
     * @param bindUserDO 登录用户信息
     * @return true 用户cookie有更新， falsecookie没有更新   有更新需要及时保存起
     */
    public boolean checkLogin(BindUserDO bindUserDO){
        this.cookieString=bindUserDO.getLoginCookie();
        try{
            steamMaFile = bindUserDO.getMaFile();
            initApiKey();
        }catch (ServiceException e){
            if(Objects.nonNull(bindUserDO.getSteamPassword()) && Objects.nonNull(bindUserDO.getMaFile())){
                login(bindUserDO.getSteamPassword(),bindUserDO.getMaFile());
            }else{
                return false;
            }
        }
        if(Objects.nonNull(bindUserDO.getLoginCookie()) && bindUserDO.getLoginCookie().equals(this.cookieString)){
            log.info("cookie不需要更新");
            return false;
        }else{
            log.info("cookie需要更新");
            return true;
        }
    }
    /**
     * 登录steam网站
     *
     * @param passwd 密码
     * @param maFile ma文件结构
     */
    private void login(String passwd, SteamMaFile maFile) {
        steamMaFile = maFile;
        //steam登录代理
        ConfigDO configByKey = configService.getConfigByKey("steam.proxy");
        HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
        builder.url(configByKey.getValue()+"login");
//        HttpUtil.HttpRequest.HttpRequestBuilder builder = HttpUtil.HttpRequest.builder();
//        builder.url(configByKey.getValue()+"login");
//        builder.url("http://172.17.0.2:25852/login");
//        builder.method(HttpUtil.Method.FORM);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("username", steamMaFile.getAccountName());
        stringStringHashMap.put("password", passwd);
        stringStringHashMap.put("token_code", steamMaFile.getSharedSecret());
        builder.form(stringStringHashMap);
        try{
            HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
            if(Objects.nonNull(proxyResponseVo.getStatus()) && proxyResponseVo.getStatus()==200){
                SteamCookie steamCookie = objectMapper.readValue(proxyResponseVo.getHtml(), SteamCookie.class);
                if (steamCookie.getCode() != 0) {
                    log.error("Steam通讯失败{}", steamCookie);
                    throw new ServiceException(-1, steamCookie.getMsg());
                }
                cookieString = steamCookie.getData().getCookie();
            }else{
                throw new ServiceException(-1,"通讯失败");
            }
        }catch (Exception e){
            throw new ServiceException(-1,"登录失败原因："+e.getMessage());
        }
        initApiKey();
    }

    /**
     * 初始化apikey session browserid等数据为必调接口
     */
    private void initApiKey() {
        HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
        builder.url("https://steamcommunity.com/dev/apikey");
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        builder.headers(header);
        builder.cookieString(cookieString);
        HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
        if(Objects.isNull(proxyResponseVo.getStatus()) || proxyResponseVo.getStatus()!=200){
            throw new ServiceException(-1, "apiKey失败");
        }
        Pattern pattern = Pattern.compile("密钥: (.*?)<"); // 正则表达式匹配API密钥
        Matcher matcher = pattern.matcher(proxyResponseVo.getHtml());
        if (matcher.find()) {
            webApiKey = Optional.of(matcher.group(1));
        }
        Pattern pattern2 = Pattern.compile("https://steamcommunity.com/profiles/(\\d+)/");
        Matcher matcher2 = pattern2.matcher(proxyResponseVo.getHtml());
        if (matcher2.find()) {
            steamId = Optional.of(matcher2.group(1));
        }
        //set browserid
        Map<String, String> cookies = proxyResponseVo.getCookies();
        if(cookies.isEmpty()){
            String[] split = cookieString.split(";");
            for(int i=0;i<split.length;i++){
                String[] split1 = split[i].split("=");
                if(split1[0].trim().equals("browserid")){
                    this.browserid = Optional.of(split1[1].trim());
                }
                if(split1[0].trim().equals("sessionid")){
                    this.sessionId = Optional.of(split1[1].trim());
                }
            }
            if(!sessionId.isPresent()){
                throw new ServiceException(-1, "获取sessionID失败");
            }
            if(!browserid.isPresent()){
                throw new ServiceException(-1, "获取浏览器ID失败");
            }
        }else{
            String browserid = cookies.get("browserid");
            if (Objects.nonNull(browserid)) {
                this.browserid = Optional.of(browserid);
                cookieString += ";browserid=" + this.browserid.get();
            } else {
                throw new ServiceException(-1, "获取浏览器ID失败");
            }
            String sessionid = cookies.get("sessionid");
            if (Objects.nonNull(sessionid)) {
                sessionId = Optional.of(sessionid);
                cookieString += ";sessionid=" + sessionid;
            } else {
                throw new ServiceException(-1, "获取sessionID失败");
            }
        }

        //提取用户名
        String regex2="(?<=user_avatar playerAvatar offline\" aria-label=\"查看您的个人资料\">\\s{0,20}<img src=\")(.*?)(?=\">)";
        Pattern patternUser = Pattern.compile(regex2, Pattern.DOTALL);
        Matcher matcherUser = patternUser.matcher(proxyResponseVo.getHtml());
        if (matcherUser.find()) {
            String imgSrc = matcherUser.group(1);
            String[] split = imgSrc.split("\" alt=\"");
            if(split.length>=2){
                String s = split[0].replaceAll(".jpg", "_full.jpg").replaceAll(".png", "_full.png");
                steamName= Optional.ofNullable(split[1]);
                steamAvatar= Optional.of(s);
            }
        }
    }

    /**
     * 初始化交易链接
     */
    public void initTradeUrl() {
        if (!steamId.isPresent()) {
            initApiKey();
        }
        if (!steamId.isPresent()) {
            throw new ServiceException(-1, "初始化steam失败，请重新登录");
        }
        HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
        builder.url("https://steamcommunity.com/profiles/:steamId/tradeoffers/privacy").cookieString(cookieString);
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        builder.headers(header);
        Map<String, String> pathVar = new HashMap<>();
        pathVar.put("steamId", steamId.get());
        builder.pathVar(pathVar);
        HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
        if(Objects.isNull(proxyResponseVo.getStatus()) || proxyResponseVo.getStatus()!=200){
            throw new ServiceException(-1, "初始化steam失败");
        }
        Pattern pattern = Pattern.compile("value=\"(.*?)\"");
        Matcher matcher = pattern.matcher(proxyResponseVo.getHtml());
        if (matcher.find()) {
            treadUrl = Optional.of(matcher.group(1));
        } else {
            log.error("获取tradeUrl失败steamId{}", steamId);
            throw new ServiceException(-1, "获取tradeUrl失败");
        }
    }

    /**
     * 验证交易链接是否可惊交易,
     * Steam帐号交易链接检测，只有返回OK的才是能进行正常交易的
     * 不能检测自己的帐号
     * @param tradeUrl
     */
    public TradeUrlStatus checkTradeUrl(String tradeUrl){
        if(Objects.isNull(tradeUrl)){
            return TradeUrlStatus.ERRORURL;
        }
        if(!tradeUrl.startsWith("https://steamcommunity.com/tradeoffer/new/")){
            return TradeUrlStatus.ERRORURL;
        }
        URI uri = URI.create(tradeUrl);
        try {
            Map<String, String> query = parseQuery(uri.getQuery());
            if(Objects.isNull(query.get("partner"))){
                return TradeUrlStatus.ERRORURL;
            }
            if(Objects.isNull(query.get("token"))){
                return TradeUrlStatus.ERRORURL;
            }
        } catch (UnsupportedEncodingException e) {
            return TradeUrlStatus.ERRORURL;
        }
        try{
            HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
            builder.url(tradeUrl).cookieString(cookieString);
            Map<String, String> header = new HashMap<>();
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            builder.headers(header);
            Map<String, String> pathVar = new HashMap<>();
            pathVar.put("steamId", steamId.get());
            builder.pathVar(pathVar);
            HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
            if(Objects.isNull(proxyResponseVo.getStatus()) || proxyResponseVo.getStatus()!=200){
                throw new ServiceException(-1, "初始化steam失败");
            }
            String html = proxyResponseVo.getHtml();

            if (html.contains("此用户帐户功能受限")) {
                return TradeUrlStatus.LIMIT;
            }
            if (html.contains("选择一个库存以查看您可以交易的物品")) {
                return TradeUrlStatus.OK;
            }
            if (html.contains("库存隐私设置已被设为“私密”，因此无法接受交易报价。")) {
                return TradeUrlStatus.PRIVATE;
            }
            if (html.contains("该交易 URL 不再能用于向")) {
                return TradeUrlStatus.NotAvailable;
            }
        }catch (ServiceException e){
            return TradeUrlStatus.BUSY;
        }
        return TradeUrlStatus.BUSY;
    }

    /**
     * 计算桌面令牌
     *
     * @param secret secret 桌面共享key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @Deprecated
    public static String getSteamAuthCode(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        long time = System.currentTimeMillis() / 30000; // Equivalent to Python's time() / 30
        System.out.println(time);
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(time);
        byte[] packedTime = buffer.array();
        byte[] msg = new byte[8];
        System.arraycopy(packedTime, 0, msg, 0, 8);
        byte[] decode = Base64.getDecoder().decode(secret);// Equivalent to Python's b64encode
        Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(decode, "HmacSHA1");
        sha1_HMAC.init(secretKeySpec); // Assuming secret is a valid Base64 encoded string
        byte[] mac = sha1_HMAC.doFinal(msg);
        int offset = mac[mac.length - 1] & 0x0f; // Equivalent to Python's & 0x0f
        int binary = ByteBuffer.wrap(mac, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt() & 0x7fffffff; // Equivalent to Python's struct unpack and & 0x7fffffff
        char[] codestr = "23456789BCDFGHJKMNPQRTVWXY".toCharArray(); // This part seems unnecessary as the original Python code only uses it once, so I'll assume this is correct for now
        char[] chars = new char[5];
        for (int i = 0; i < 5; i++) {
            chars[i] = codestr[binary % 26]; // Equivalent to Python's % 26 and indexing
            binary /= 26; // Equivalent to Python's //= 26
        }
        return new String(chars);
    }

    /**
     * 转换ID
     *
     * @param id
     * @return
     */
    public String toCommunityID(String id) {
        if (id.startsWith("STEAM_")) {
            String[] parts = id.split(":");
            BigInteger part1 = new BigInteger(parts[1]);
            BigInteger part2 = new BigInteger(parts[2]);
            BigInteger communityID = part1.add(part2.multiply(BigInteger.valueOf(2))).add(BigInteger.valueOf(76561197960265728L));
            return communityID.toString();
        } else if (id.matches("\\d+") && id.length() < 16) {
            BigInteger steamID = new BigInteger(id);
            BigInteger communityID = steamID.add(BigInteger.valueOf(76561197960265728L));
            return communityID.toString();
        } else {
            return id;
        }
    }

    /**
     * 我方发送报价是到对方 本接口会自动进行手机确认
     * 参考
     * https://www.coder.work/article/8018121#google_vignette
     *
     * @param steamInvDto 我方交易商品
     * @param tradeUrl    接收方交易链接 如 https://steamcommunity.com/tradeoffer/new/?partner=1432096359&token=giLGhxtN
     * @return
     */
    public SteamTradeOfferResult trade(SteamInvDto steamInvDto, String tradeUrl,String msg) {
        try {
            URI uri = URI.create(tradeUrl);
            String query = uri.getQuery();
            log.info(query);
            Map<String, String> stringStringMap = parseQuery(query);
            log.info(String.valueOf(stringStringMap));
            HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
            builder.url("https://steamcommunity.com/tradeoffer/new/send").cookieString(cookieString);
            Map<String, String> post = new HashMap<>();
            post.put("sessionid", sessionId.get());
            post.put("serverid", "1");
            post.put("partner", toCommunityID(stringStringMap.get("partner")));
            post.put("captcha", "");
            post.put("trade_offer_create_params", "{\"trade_offer_access_token\":\"" + stringStringMap.get("token") + "\"}");
            SteamTradeOffer steamTradeOffer = new SteamTradeOffer();
            steamTradeOffer.setNewversion(true);
            steamTradeOffer.setVersion(3);
            SteamTradeOffer.MeDTO meDTO = new SteamTradeOffer.MeDTO();
            meDTO.setReady(false);
            meDTO.setCurrency(new ArrayList<>());
            SteamTradeOffer.MeDTO.AssetsDTO assetsDTO = new SteamTradeOffer.MeDTO.AssetsDTO();
            assetsDTO.setAppid(steamInvDto.getAppid());
            assetsDTO.setContextid(steamInvDto.getContextid());
            assetsDTO.setAmount(Integer.valueOf(steamInvDto.getAmount()));
            assetsDTO.setAssetid(steamInvDto.getAssetid());
            meDTO.setAssets(Collections.singletonList(assetsDTO));
            steamTradeOffer.setMe(meDTO);
            SteamTradeOffer.ThemDTO themDTO = new SteamTradeOffer.ThemDTO();
            themDTO.setAssets(Collections.emptyList());
            themDTO.setCurrency(Collections.emptyList());
            themDTO.setReady(false);
            steamTradeOffer.setThem(themDTO);
            post.put("json_tradeoffer", objectMapper.writeValueAsString(steamTradeOffer));
            post.put("tradeoffermessage", msg+" 时间" + LocalDateTime.now());
            builder.form(post);
            Map<String, String> header = new HashMap<>();
            header.put("Accept-Language", "zh-CN,zh;q=0.9");
            header.put("Referer", tradeUrl);
            builder.headers(header);
            log.info("发送到对方服务器数据{}", objectMapper.writeValueAsString(builder.build()));
            HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
            log.info("交易结果{}", proxyResponseVo);
            if(Objects.isNull(proxyResponseVo.getStatus()) || proxyResponseVo.getStatus()!=200){
                throw new ServiceException(-1, "交易失败");
            }
            SteamTradeOfferResult json = objectMapper.readValue(proxyResponseVo.getHtml(), SteamTradeOfferResult.class);
            Optional<MobileConfList.ConfDTO> confDTO = confirmOfferList(json.getTradeofferid());
            if (confDTO.isPresent()) {
//                confirmOffer(confDTO.get(), ConfirmAction.CANCEL);
                confirmOffer(confDTO.get(), ConfirmAction.ALLOW);
            }else {
                log.warn("交易单据未进行手机自动确认{}",json.getTradeofferid());
            }
            return json;
        } catch (UnsupportedEncodingException e) {
            log.error("解码出错{}", e);
            throw new ServiceException(-1, "发起交易报价失败原因，解码输入出错");
        } catch (JsonProcessingException e) {
            log.error("交易出错，可能 库存不正确{}", e);
            throw new ServiceException(-1, "发起交易报价失败原因，交易出错，可能 库存不正确");
        }
    }

    /**
     * 生成确认列表签名
     *
     * @param tag
     * @return
     */
    private ConfirmHash generateConfirmationHashForTime(String tag) throws NoSuchAlgorithmException, InvalidKeyException {
        long time = System.currentTimeMillis() / 1000L;
        byte[] arr = new byte[8];
        // 将 long 类型的 time 转换为 8 个字节的大端序
        for (int i = 0; i < 8; i++) {
            arr[i] = (byte) (time >> ((7 - i) * 8));
        }

        // 将 tag 转换为 UTF-8 编码的字节数组，并确保长度不超过 32 字节
        byte[] tagBytes = tag.getBytes(StandardCharsets.UTF_8);
        if (tagBytes.length > 32) {
            tagBytes = Arrays.copyOf(tagBytes, 32);
        }

        // 将时间字节和标签字节合并
        byte[] data = new byte[arr.length + tagBytes.length];
        System.arraycopy(arr, 0, data, 0, arr.length);
        System.arraycopy(tagBytes, 0, data, arr.length, tagBytes.length);

        // 创建 HMAC-SHA1 Mac 实例
        Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
        byte[] decode = Base64.getDecoder().decode(steamMaFile.getIdentitySecret());
        SecretKeySpec secret_key = new SecretKeySpec(decode, "HmacSHA1");
        sha1_HMAC.init(secret_key);

        // 计算 HMAC 并返回 Base64 编码的哈希值
        byte[] hashedData = sha1_HMAC.doFinal(data);
        ConfirmHash confirmHash = new ConfirmHash();
        confirmHash.setHash(Base64.getEncoder().encodeToString(hashedData));
        confirmHash.setTime(time);
        return confirmHash;
    }

    public SteamResult confirmOffer(MobileConfList.ConfDTO confDTO, ConfirmAction confirmAction) {
        HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
        builder.url("https://steamcommunity.com/mobileconf/ajaxop").cookieString(cookieString);
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        builder.headers(header);
        Map<String, String> query = new HashMap<>();
        query.put("op", confirmAction.getCode());
        query.put("cid", confDTO.getId());
        query.put("ck", confDTO.getNonce());
        query.put("p", steamMaFile.getDeviceId());
        query.put("a", steamId.get());
        query.put("m", "react");
        String tag = "reject";
        switch (confirmAction) {
            case ALLOW:
                tag = "accept";
                break;
            case CANCEL:
                tag = "reject";
                break;
            default:
                throw new ServiceException(-1, "不支持的操作");
        }
        query.put("tag", tag);
        try {
            ConfirmHash confirmHash = generateConfirmationHashForTime(tag);
            query.put("k", confirmHash.getHash());
            query.put("t", String.valueOf(confirmHash.getTime()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        builder.query(query);
        HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
        if(Objects.isNull(proxyResponseVo.getStatus()) || proxyResponseVo.getStatus()!=200){
            throw new ServiceException(-1, "确认订单失败");
        }

        SteamResult json;
        try {
            json = objectMapper.readValue(proxyResponseVo.getHtml(), SteamResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("确认订单失败原因{}",e.getMessage());
            throw new ServiceException(-1, "确认订单失败2");
        }
        return json;
    }

    /**
     * 获取指定待手机确认的订单
     *
     * @param tradeOrderId 订单号
     * @return
     */
    public Optional<MobileConfList.ConfDTO> confirmOfferList(String tradeOrderId) {
        List<MobileConfList.ConfDTO> confDTOS = confirmOfferList();
        return confDTOS.stream().filter(item -> item.getCreatorId().equals(tradeOrderId)).findFirst();
    }
    /**
     * 获取所有待手机确认的订单
     * @return
     */
    public List<MobileConfList.ConfDTO> confirmOfferList() {
        HttpUtil.ProxyRequestVo.ProxyRequestVoBuilder builder = HttpUtil.ProxyRequestVo.builder();
        builder.url("https://steamcommunity.com/mobileconf/getlist").cookieString(cookieString);
        Map<String, String> query = new HashMap<>();
        query.put("p", steamMaFile.getDeviceId());
        query.put("a", steamId.get());
        try {
            ConfirmHash confirmHash = generateConfirmationHashForTime("conf");
            query.put("k", confirmHash.getHash());
            query.put("t", String.valueOf(confirmHash.getTime()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        query.put("m", "react");
        query.put("tag", "conf");
        builder.query(query);
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        builder.headers(header);
        MobileConfList json;
        try {
            log.info("发送到对方服务器数据{}", objectMapper.writeValueAsString(builder.build()));
            HttpUtil.ProxyResponseVo proxyResponseVo = HttpUtil.sentToSteamByProxy(builder.build());
            log.info("交易结果{}", proxyResponseVo);
            if(Objects.isNull(proxyResponseVo.getStatus()) || proxyResponseVo.getStatus()!=200){
                throw new ServiceException(-1, "交易失败");
            }
            json = objectMapper.readValue(proxyResponseVo.getHtml(), MobileConfList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("交易失败服务器返回内容不可识别{}",builder.build());
            throw new ServiceException(-1, "交易失败服务器返回内容不可识别");
        }
        return json.getConf();
    }
    public Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> ret = new HashMap<>();
        String[] split = query.split("&");
        for (String s : split) {
            String[] split1 = s.split("=");
            ret.put(split1[0], URLDecoder.decode(split1[1], "utf-8"));
        }
        return ret;
    }
//    public static void main(String[] args) {
//
//        String maFileString = "{\"shared_secret\":\"NTDfP+vPKSLS2O8lXKJgdAp5QRI=\",\"serial_number\":\"3438498994078918164\",\"revocation_code\":\"R34847\",\"uri\":\"otpauth://totp/Steam:6wws6f?secret=GUYN6P7LZ4USFUWY54SVZITAOQFHSQIS&issuer=Steam\",\"server_time\":1701078470,\"account_name\":\"6wws6f\",\"token_gid\":\"2fba3858dfc17a80\",\"identity_secret\":\"q4roYdO+Cz/QOSVHB5m7pbYAyCc=\",\"secret_1\":\"m6OeZ+3cuVr/I/kEK2tjqhgbzSs=\",\"status\":1,\"device_id\":\"android:06ad9382-4690-45c1-90e9-2fc31cf803dc\",\"fully_enrolled\":true,\"Session\":{\"SteamID\":76561199392362087,\"AccessToken\":\"eyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInI6MEU3Nl8yM0VDNjYwQV9FNDA1MiIsICJzdWIiOiAiNzY1NjExOTkzOTIzNjIwODciLCAiYXVkIjogWyAid2ViIiwgIm1vYmlsZSIgXSwgImV4cCI6IDE3MDczNzI1MjEsICJuYmYiOiAxNjk4NjQ0Mzc5LCAiaWF0IjogMTcwNzI4NDM3OSwgImp0aSI6ICIwRTdCXzIzRUM2NjEwX0RFREQ0IiwgIm9hdCI6IDE3MDcyODQzNzksICJydF9leHAiOiAxNzI1NTcyOTA5LCAicGVyIjogMCwgImlwX3N1YmplY3QiOiAiMTEzLjI1MS45OS4yMTIiLCAiaXBfY29uZmlybWVyIjogIjExMy4yNTEuOTkuMjEyIiB9.MwVd4CC8DZcjENIKU9X4zDrHYauOsBclYEoyvfOsOtxOM8sYkhhwW9PxzjlweFa7_tkpSKrkXw13Quhwp6X3CA\",\"RefreshToken\":\"eyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0.eyAiaXNzIjogInN0ZWFtIiwgInN1YiI6ICI3NjU2MTE5OTM5MjM2MjA4NyIsICJhdWQiOiBbICJ3ZWIiLCAicmVuZXciLCAiZGVyaXZlIiwgIm1vYmlsZSIgXSwgImV4cCI6IDE3MjU1NzI5MDksICJuYmYiOiAxNjk4NjQ0Mzc5LCAiaWF0IjogMTcwNzI4NDM3OSwgImp0aSI6ICIwRTc2XzIzRUM2NjBBX0U0MDUyIiwgIm9hdCI6IDE3MDcyODQzNzksICJwZXIiOiAxLCAiaXBfc3ViamVjdCI6ICIxMTMuMjUxLjk5LjIxMiIsICJpcF9jb25maXJtZXIiOiAiMTEzLjI1MS45OS4yMTIiIH0.zYwXhy5shkMyjUJ4s42bbKovBSk6LTmo9-JFUHoGRpUKBRqexr9k35olVDezMX3MVIZ8nN5gwnXYLGQ9ayVWAQ\",\"SessionID\":null}}";
//
//        SteamWeb steamWeb = new SteamWeb(null);
//        try {
//            SteamMaFile steamMaFile = steamWeb.objectMapper.readValue(maFileString, SteamMaFile.class);
////            steamWeb.login("QFhG9jSs", steamMaFile);
//            steamWeb.initApiKey();
//            log.info(steamWeb.steamName.get());
//            log.info(steamWeb.steamAvatar.get());
//////            TradeUrlStatus tradeUrlStatus = steamWeb.checkTradeUrl("https://steamcommunity.com/tradeoffer/new/?partner=66353311&token=EOt4K8X5");
//////            steamWeb.initApiKey();
////            String tradeUrl = "https://steamcommunity.com/tradeoffer/new/?partner=1440000356&token=5_JGX9AA";
////            SteamInvDto steamInvDto = new SteamInvDto();
////            steamInvDto.setAppid(730);
////            steamInvDto.setContextid("2");
////            steamInvDto.setAssetid("35965237262");
////            steamInvDto.setInstanceid("302028390");
////            steamInvDto.setClassid("5729789337");
////            steamInvDto.setAmount("1");
////            SteamTradeOfferResult trade = steamWeb.trade(steamInvDto, tradeUrl);
////            log.info("将临信息{}", trade);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
}
