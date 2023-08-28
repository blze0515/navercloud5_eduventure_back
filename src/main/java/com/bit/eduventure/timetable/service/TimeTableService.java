package com.bit.eduventure.timetable.service;

import com.bit.eduventure.ES3_Course.Service.CourseService;
import com.bit.eduventure.timetable.dto.TimeTableDTO;
import com.bit.eduventure.ES3_Course.Entity.Course;
import com.bit.eduventure.ES3_Course.Repository.CourseRepository;
import com.bit.eduventure.ES1_User.Repository.UserRepository;
import com.bit.eduventure.timetable.entity.TimeTable;
import com.bit.eduventure.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final CourseRepository courseRepository;
    private final TimeTableRepository timeTableRepository;

    /* 시간표 등록 */
    public void  registerTimetable(TimeTableDTO requestDTO) {

        // TimeTableDTO
        TimeTableDTO tableDTO = TimeTableDTO.builder()
                .couNo(requestDTO.getCouNo())
                .timeNo(requestDTO.getTimeNo())
                .claName(requestDTO.getClaName())
                .timeWeek(requestDTO.getTimeWeek())
                .timeClass(requestDTO.getTimeClass())
                .timePlace(requestDTO.getTimePlace())
                .timeColor(requestDTO.getTimeColor())
                .timeTitle(requestDTO.getTimeTitle())
                .timeTeacher(requestDTO.getTimeTeacher())
                .build();

        // 데이터베이스에 저장
        timeTableRepository.save(tableDTO.DTOTOEntity());
    }

    /* 시간표 조회 */
//    public TimeTableDTO getTimetable(int timeNo) {
//        TimeTable timeTable = timeTableRepository.findById(timeNo).get();
//        Course course = courseRepository.findByClaName(timeTable.getClaName());
//
//        TimeTableDTO dto = TimeTableDTO.builder()
//                .timeNo(timeTable.getTimeNo())
//                .couNo(course.getCouNo())
//                .claName(course.getClaName())
//                .timeWeek(timeTable.getTimeWeek())
//                .timeClass(timeTable.getTimeClass())
//                .timePlace(timeTable.getTimePlace())
//                .timeColor(timeTable.getTimeColor())
//                .timeTitle(timeTable.getTimeTitle())
//                .timeTeacher(timeTable.getTimeTeacher())
//                .build();
//
//        return dto;
//    }

    /* couNo을 기반으로 TimeTable 목록 조회 */
    public List<TimeTableDTO> getTimetablesByCouNo(int couNo) {
        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(couNo);

        List<TimeTableDTO> returnList = new ArrayList<>();

        for (TimeTable timeTable : timeTableList) {
            TimeTableDTO dto = timeTable.EntityTODTO();
            returnList.add(dto);
        }
        return returnList;
    }



    public List<String> getTimeWeekByCouNo(int couNo) {
        List<TimeTableDTO> dtos = getTimetablesByCouNo(couNo);
        return dtos.stream()
                .map(TimeTableDTO::getTimeWeek)
                .collect(Collectors.toList());
    }


    /* 시간표 전체 조회 */
    public List<TimeTableDTO> getAllTimetables() {

        List<TimeTable> timeTableList = timeTableRepository.findAll();
        List<TimeTableDTO> returnList = new ArrayList<>();
        System.out.println("시간표 서비스 returnList1==========="+returnList);

        for (TimeTable timeTable : timeTableList) {
            Course course = courseRepository.findByClaName(timeTable.getClaName());

            TimeTableDTO dto = TimeTableDTO.builder()
                    .timeNo(timeTable.getTimeNo())
                    .couNo(course.getCouNo())
                    .claName(course.getClaName())
                    .timeWeek(timeTable.getTimeWeek())
                    .timeClass(timeTable.getTimeClass())
                    .timePlace(timeTable.getTimePlace())
                    .timeColor(timeTable.getTimeColor())
                    .timeTitle(timeTable.getTimeTitle())
                    .timeTeacher(timeTable.getTimeTeacher())
                    .build();
            returnList.add(dto);
        }

        System.out.println("시간표 서비스 returnList2=============="+returnList);
        return returnList;
    }

    /* 시간표 삭제 */
    public void deleteTimetable(String claName, String timeWeek) {
        List<TimeTable> timeTables = timeTableRepository.findByClaNameAndTimeWeek(claName, timeWeek);
        if (!timeTables.isEmpty()) {
            for (TimeTable timeTable : timeTables) {
                timeTableRepository.delete(timeTable);
            }
        } else {
            throw new IllegalArgumentException("TimeTable not found");
        }
    }

    /* 학생별 시간표 리스트 */
    public List<TimeTableDTO> getTimetablesByStudent(int couNo) {

        List<TimeTable> timeTableList = timeTableRepository.findTimeTablesByUserId(couNo);
        List<TimeTableDTO> returnList = new ArrayList<>();
        System.out.println("시간표 서비스 returnList1==========="+returnList);

        for (TimeTable timeTable : timeTableList) {
            Course course = courseRepository.findByClaName(timeTable.getClaName());

            TimeTableDTO dto = TimeTableDTO.builder()
                    .timeNo(timeTable.getTimeNo())
                    .couNo(timeTable.getTimeNo())
                    .claName(course.getClaName())
                    .timeWeek(timeTable.getTimeWeek())
                    .timeClass(timeTable.getTimeClass())
                    .timePlace(timeTable.getTimePlace())
                    .timeColor(timeTable.getTimeColor())
                    .timeTitle(timeTable.getTimeTitle())
                    .timeTeacher(timeTable.getTimeTeacher())
                    .build();
            returnList.add(dto);
        }

        System.out.println("시간표 서비스 returnList2=============="+returnList);
        return returnList;
    }

    public List<TimeTable> getTimeTableListForClaName(String claName) {
        return timeTableRepository.findAllByClaName(claName);
    }

}