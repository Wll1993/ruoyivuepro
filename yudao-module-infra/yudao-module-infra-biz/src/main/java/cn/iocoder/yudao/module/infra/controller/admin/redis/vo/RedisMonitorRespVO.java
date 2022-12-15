package cn.iocoder.yudao.module.infra.controller.admin.redis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Properties;

@Schema(title = "管理后台 - Redis 监控信息 Response VO")
@Data
@Builder
@AllArgsConstructor
public class RedisMonitorRespVO {

    @Schema(title = "Redis info 指令结果", requiredMode = Schema.RequiredMode.REQUIRED, description = "具体字段，查看 Redis 文档")
    private Properties info;

    @Schema(title = "Redis key 数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long dbSize;

    @Schema(title = "CommandStat 数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CommandStat> commandStats;

    @Schema(title = "Redis 命令统计结果")
    @Data
    @Builder
    @AllArgsConstructor
    public static class CommandStat {

        @Schema(title = "Redis 命令", requiredMode = Schema.RequiredMode.REQUIRED, example = "get")
        private String command;

        @Schema(title = "调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long calls;

        @Schema(title = "消耗 CPU 秒数", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
        private Long usec;

    }

}
