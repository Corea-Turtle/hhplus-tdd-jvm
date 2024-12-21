package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    public PointController(PointService pointservice) {
        this.pointService = pointservice;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     * 포인트를 조회할 떄 필요한 것 - userId의 인증기능(인증기능은 나중에 따로 만든다.)
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return pointService.pointInquiry(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return pointService.pointHistoryListInquiry(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     *    충전 정책
     *    - 1회당 최소 충전 1,000
     *    - 1회당 최대 충전 2,000,000,000
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        try{
            if(id <= 0){
                System.out.println("아이디가 이상합니다.");
            }
            if(amount < 1000){
                System.out.println("충전 최소 금액은 1000이상 가능합니다.");
            }
            if(amount > 2000000000){
                System.out.println("1회 충전 최대 금액은 2,000,000,000이상 가능합니다.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //충전시 현재 유저의 포인트를 조회
        UserPoint curUserPoint = pointService.pointInquiry(id);
        long curAmount = curUserPoint.point();

        return curUserPoint;
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }
}
