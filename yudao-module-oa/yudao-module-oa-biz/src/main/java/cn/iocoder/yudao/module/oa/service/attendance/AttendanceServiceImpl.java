package cn.iocoder.yudao.module.oa.service.attendance;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.attendance.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.attendance.AttendanceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.oa.convert.attendance.AttendanceConvert;
import cn.iocoder.yudao.module.oa.dal.mysql.attendance.AttendanceMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 考勤打卡 Service 实现类
 *
 * @author 东海
 */
@Service
@Validated
public class AttendanceServiceImpl implements AttendanceService {

    @Resource
    private AttendanceMapper attendanceMapper;

    @Override
    public Long createAttendance(AttendanceCreateReqVO createReqVO) {
        // 插入
        AttendanceDO attendance = AttendanceConvert.INSTANCE.convert(createReqVO);
        attendanceMapper.insert(attendance);
        // 返回
        return attendance.getId();
    }

    @Override
    public void updateAttendance(AttendanceUpdateReqVO updateReqVO) {
        // 校验存在
        validateAttendanceExists(updateReqVO.getId());
        // 更新
        AttendanceDO updateObj = AttendanceConvert.INSTANCE.convert(updateReqVO);
        attendanceMapper.updateById(updateObj);
    }

    @Override
    public void deleteAttendance(Long id) {
        // 校验存在
        validateAttendanceExists(id);
        // 删除
        attendanceMapper.deleteById(id);
    }

    private void validateAttendanceExists(Long id) {
        if (attendanceMapper.selectById(id) == null) {
            throw exception(ATTENDANCE_NOT_EXISTS);
        }
    }

    @Override
    public AttendanceDO getAttendance(Long id) {
        return attendanceMapper.selectById(id);
    }

    @Override
    public List<AttendanceDO> getAttendanceList(Collection<Long> ids) {
        return attendanceMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<AttendanceDO> getAttendancePage(AttendancePageReqVO pageReqVO) {
        return attendanceMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AttendanceDO> getAttendanceList(AttendanceExportReqVO exportReqVO) {
        return attendanceMapper.selectList(exportReqVO);
    }

}
