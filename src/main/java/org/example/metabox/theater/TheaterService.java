package org.example.metabox.theater;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.metabox._core.errors.exception.Exception401;
import org.example.metabox._core.errors.exception.Exception404;
import org.example.metabox.movie.MovieRepository;
import org.example.metabox.screening_info.ScreeningInfo;
import org.example.metabox.screening_info.ScreeningInfoRepository;
import org.example.metabox.theater_scrap.TheaterScrap;
import org.example.metabox.theater_scrap.TheaterScrapRepository;
import org.example.metabox.user.SessionUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterRepository theaterRepository;
    private final TheaterScrapRepository theaterScrapRepository;
    private final MovieRepository movieRepository;
    private final ScreeningInfoRepository screeningInfoRepository;

    @Transactional
    public TheaterResponse.TheaterDTO movieSchedule(SessionUser sessionUser, Integer theaterId, LocalDate date) {
        // 1. 내가 Scrap한 목록 불러오기
        List<TheaterScrap> theaterScrapList = new ArrayList<>();
        if (sessionUser == null) {
            while (theaterScrapList.size() < 5) {
                theaterScrapList.add(TheaterScrap.builder().id(0).theater(Theater.builder().name("").build()).build());
            }
        } else {
            theaterScrapList = theaterScrapRepository.findByUserId(sessionUser.getId());
            // 무조건 theaterScrapList의 사이즈가 5가 되도록 설정
            while (theaterScrapList.size() < 5) {
                theaterScrapList.add(TheaterScrap.builder().id(0).theater(Theater.builder().name("").build()).build());
            }
        }
        // 2. 지역 목록에 따른 극장 목록 가져오기
        List<Theater> theaterList = theaterRepository.findAll();

        // 3. ScreeningInfo 가져오기

        List<ScreeningInfo> screeningInfoList = screeningInfoRepository.findByTheaterId(theaterId, date);

        // 4. theater 가져오기
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new Exception404("극장을 찾을 수 없습니다."));

        // 리턴
        TheaterResponse.TheaterDTO respDTO = new TheaterResponse.TheaterDTO(theaterScrapList, theaterList, screeningInfoList, theater);
        return respDTO;
    }

    @Transactional
    public TheaterResponse.TheaterInfoDTO theaterInfo(SessionUser sessionUser, Integer theaterId) {
        // 1. 내가 Scrap한 목록 불러오기
        List<TheaterScrap> theaterScrapList = new ArrayList<>();
        if (sessionUser == null) {
            while (theaterScrapList.size() < 5) {
                theaterScrapList.add(TheaterScrap.builder().id(0).theater(Theater.builder().name("").build()).build());
            }
        } else {
            theaterScrapList = theaterScrapRepository.findByUserId(sessionUser.getId());
            // 무조건 theaterScrapList의 사이즈가 5가 되도록 설정
            while (theaterScrapList.size() < 5) {
                theaterScrapList.add(TheaterScrap.builder().id(0).theater(Theater.builder().name("").build()).build());
            }
        }
        // 2. 지역 목록에 따른 극장 목록 가져오기
        List<Theater> theaterList = theaterRepository.findAll();

        //3. theater 가져오기
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new Exception404("극장을 찾을 수 없습니다."));

        TheaterResponse.TheaterInfoDTO respDTO = new TheaterResponse.TheaterInfoDTO(theaterScrapList, theaterList, theater);
        return respDTO;
    }

    @Transactional
    public TheaterResponse.TheaterAjaxDTO movieScheduleDate(Integer theaterId, LocalDate date) {
        List<ScreeningInfo> screeningInfoList = screeningInfoRepository.findByTheaterId(theaterId, date);
        TheaterResponse.TheaterAjaxDTO respDTO = new TheaterResponse.TheaterAjaxDTO(screeningInfoList);
        return respDTO;
    }

    public Theater login(TheaterRequest.LoginDTO reqDTO) {
        System.out.println("아이디 : " + reqDTO.getLoginId());
        System.out.println("비밀번호 : " + reqDTO.getPassword());
        Theater theater = theaterRepository.findByLoginIdAndPassword(reqDTO.getLoginId(), reqDTO.getPassword()).orElseThrow(() -> new Exception401("아이디 또는 비밀번호가 틀렸습니다."));
        return theater;
    }
}
