package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 포인트 서비스에 들어가야하는 기능
 *   총 4가지 기본 기능 (포인트 조회, 포인트 충전/사용 내역 조회, 충전, 사용) 을 구현합니다.
 *
 * - 포인트 충전, 사용에 대한 정책 추가 (잔고 부족, 최대 잔고 등)
 * - 동시에 여러 요청이 들어오더라도 순서대로 (혹은 한번에 하나의 요청씩만) 제어될 수 있도록 리팩토링
 * - 동시성 제어에 대한 통합 테스트 작성
 *
 *  포인트 충전, 사용에 대한 정책
 *
 *  충전 정책
 *  - 1회당 최소 충전 1,000
 *  - 1회당 최대 충전 2,000,000,000
 *
 *  사용 정책
 *  - 잔여 사용량 초과하여 사용할 수 없다.
 *
 *
 * 1.요구사항이 실패하는 경우를 먼저 생각.
 *
 *   1-1. 포인트 조회 실패 상황
 *      - 아이디 압룍 값이 long이 아닌 자료형일 경우. 특수문자 비허용.
 *      - (매개변수가 아이디이므로) 타인의 아이디 조회는 불가능 하다.
 *
 *   1-2. 포인트 충전/사용 내역 조회 실패 상황
 *      - 아이디 입력 값이 long이 아닌 자료형일 경우. 특수문자 비허용.
 *      - (매개변수가 아이디이므로) 타인의 아이디 충전/사용 내역 조회는 불가능하다.
 *
 *   1-3. 포인트 충전 실패 상황
 *      - 최대 잔여 포인트를 넘을 수 없다.
 *      - 1회당 최대충전을 넘을 수 없다.
 *      - 최소 충전값 이하로 충전시 실패.
 *      - 동일한 충전 요청이 짧은 시간 내에 여러번 들어왔을때.
 *      - 충전은 되었는데 충전 내역에 기록이 되지 않았다?
 *
 *  1-4 포인트 사용 실패 상황
 *      -잔여 사용량을 초과하여 사용할 수 없다.
 *      -사용은 되었는데 사용 내역에 기록이 되지 않았다?
 *
 *
 * 2.요구사항이 성공하는 경우
 *
 *  2-1. 포인트 조회 성공
 *  2-2. 호인트/충전 사용 내역 조회 성공
 *  2-3. 포인트 충전이 성공한다.
 *       -동시에 여러 요청이 들어올시 순차 처리(기준: 요청 시간)
 *       -만약 여러 유저에 대한 처리라면 멀티스레드로 처리
 *  2-4. 포인트 사용이 성공한다.
 *       -동시에 여러 요청이 들어올시 순차 처리(기준: 요청 시간)
 *       -만약 여러 유저에 대한 처리라면 멀티스레드로 처리
 */


@Service
public class PointService {

    @Autowired
    PointHistoryTable pointHistoryTable;

    @Autowired
    UserPointTable userPointTable;

    /**
     * 포인트 조회
     * @param id
     * @return
     */
    public UserPoint pointInquiry(long id){
        return userPointTable.selectById(id);
    }


    /**
     * 포인트/충전 사용 내역 조회
     * @param id
     * @return
     */
    public List<PointHistory> pointHistoryListInquiry(long id){
        return pointHistoryTable.selectAllByUserId(id);
    }


    /**
     * 포인트 충전/사용
     * @param id
     * @param amount
     * @return
     */
    public UserPoint pointChargeAndUse(long id, long amount){
        return userPointTable.insertOrUpdate(id, amount);
    }


    /**
     * 포인트/충전 사용 기록
     * @param id
     * @param amount
     * @param type
     * @param updateMillis
     * @return
     */
    public PointHistory insertPointHistory(long id, long amount, TransactionType type, long updateMillis){
        return pointHistoryTable.insert(id, amount, type, updateMillis);
    }

}
