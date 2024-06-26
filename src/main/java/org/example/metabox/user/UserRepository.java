package org.example.metabox.user;

import org.example.metabox.movie_scrap.MovieScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 내가 본 영화용 (전체 내역)
//    SELECT m.title, m.img_filename, si.date as "관람일시", si.show_time, si.start_time as "시작시간", si.end_time as "종료시간", b.id, s.name, t.name, b.user_id as "유저"
//    FROM book_tb b
//    INNER JOIN seat_book_tb sb ON sb.book_id = b.id
//    INNER JOIN screening_info_tb si ON sb.screening_info_id = si.id
//    INNER JOIN screening_tb s ON s.id = si.screening_id
//    INNER JOIN theater_tb t ON s.theater_id = t.id
//    INNER JOIN movie_tb m ON si.movie_id = m.id
//    WHERE user_id = 1;



    // OAuth 중복 닉네임 있는지 확인 쿼리
    @Query("select u from User u where u.nickname = :nickname")
    User findByNickname(String nickname);

    // OAuth 회원탈톼 (pk로 해야하면 말해주세여)
    @Modifying
    @Query("delete from User u where u.nickname = :nickname")
    void deleteByNickname(String nickname);


}
