package com.scnujxjy.backendpoint.util.filter;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.DegreeInfoRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleFilterDataVO;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定义信息筛选接口，用于实现平台各种角色的信息查询
 */
public abstract class AbstractFilter {
    @Resource
    protected PlatformUserMapper platformUserMapper;

    @Resource
    protected CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    protected CollegeInformationMapper collegeInformationMapper;

    @Resource
    protected CourseScheduleMapper courseScheduleMapper;

    @Resource
    protected StudentStatusMapper studentStatusMapper;

    @Resource
    protected ClassInformationMapper classInformationMapper;

    /**
     * 筛选学籍数据的方法
     * @param data 获取的学籍数据
     * @return 学籍数据集合
     */
    public List<StudentStatusPO> filterStudentInfo(List<StudentStatusPO> data) {
        // 默认实现，子类可以选择性地重写
        return data;
    }

    /**
     * 筛选学位数据的方法
     * @param degreeFilter 获取的学位数据
     * @return 学位数据集合
     */
    public List<DegreeInfoPO> filterDegreeInfo(DegreeInfoRO degreeFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 筛选排课表数据的方法
     * @param courseScheduleFilter 获取的排课表数据
     * @return 排课表数据集合
     */
    public CourseScheduleFilterDataVO filterCourseSchedule(PageRO<CourseScheduleRO> courseScheduleFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }
}
