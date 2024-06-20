package org.example.metabox.movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.metabox._core.util.FileUtil;
import org.example.metabox.movie_pic.MoviePic;
import org.example.metabox.movie_pic.MoviePicRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final FileUtil fileUtil;
    private final MoviePicRepository moviePicRepository;

    // 모든 영화를 조회하는 메서드
    public List<MovieResponse.MovieChartDTO> getAllMovies() {
        // movieRepository를 사용하여 모든 Movie 객체를 데이터베이스로부터 가져옵니다.
        List<Movie> movies = movieRepository.findAll();

        // 가져온 Movie 객체들을 MovieResponse.MovieDTO 객체로 변환하여 리스트로 만듭니다.
        return movies.stream()
                     .map(MovieResponse.MovieChartDTO::new)
                     .collect(Collectors.toList());
    }

    // movieId에 해당하는 상세 정보를 조회하는 메서드
    public MovieResponse.MovieDetailDTO findById(Integer movieId) {
        // movieId에 해당하는 영화 정보를 데이터베이스에서 조회합니다.
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("해당 영화가 존재하지 않습니다. " + movieId));

        // 영화 개봉 상태를 계산합니다.
        String status = checkMovieReleaseStatus(movie.getDate());

        // 조회한 영화 정보를 MovieDetailDTO로 변환하여 반환합니다.
        return MovieResponse.MovieDetailDTO.formEntity(movie, status);
    }

    // 상영 상태를 확인하는 메서드
    public String checkMovieReleaseStatus(Date releaseDate) {
        // 현재 날짜를 LocalDate 객체로 가져옵니다.
        LocalDate today = LocalDate.now();
        // 영화 개봉일을 LocalDate 객체로 변환합니다.
        LocalDate movieReleaseDate = releaseDate.toLocalDate();

        // 개봉일이 오늘 이전이거나 오늘과 같으면 "현재상영중"을 반환합니다.
        if (movieReleaseDate.isBefore(today) || movieReleaseDate.isEqual(today)) {
            return "현재상영중";
        } else {
            // 개봉일이 오늘 이후인 경우, 오늘부터 개봉일까지의 일수를 계산합니다.
            long dDay = ChronoUnit.DAYS.between(today, movieReleaseDate);
            // "상영예정 D-일수" 형식으로 반환합니다.
            return "상영예정 D-" + dDay;
        }
    }

    // 영화 등록 메서드
    public Movie addMovie(MovieRequest.movieSavaFormDTO reqDTO) {
        // MultipartFile 객체로부터 포스터 파일 가져오기
        MultipartFile poster = reqDTO.getImgFilename();
        String posterFileName = null;

        try {
            // 포스터 파일 저장 및 파일 이름 설정
            posterFileName = fileUtil.saveMoviePoster(poster);
        } catch (IOException e) {
            // 파일 저장 중 예외 발생 시 런타임 예외로 전환
            throw new RuntimeException("이미지 오류", e);
        }

        // Movie 객체 빌더 패턴으로 생성
        Movie movie = Movie.builder()
                .title(reqDTO.getTitle())               // 영화 제목 설정
                .engTitle(reqDTO.getEngTitle())         // 영어 제목 설정
                .director(reqDTO.getDirector())         // 감독 설정
                .actor(reqDTO.getActor())               // 배우 설정
                .genre(reqDTO.getGenre())               // 장르 설정
                .info(reqDTO.getInfo())                 // 기본 정보 설정
                .date(reqDTO.getDate())                 // 개봉일 설정
                .imgFilename(posterFileName)            // 포스터 파일 이름 설정
                .description(reqDTO.getDescription())   // 영화 설명 설정
                .build();

        // Movie 객체를 데이터베이스에 저장하여 PK 값 생성
        movie = movieRepository.save(movie);

        // 스틸컷 이미지 파일 처리
        List<MoviePic> moviePicList = new ArrayList<>();
        MultipartFile[] stills = reqDTO.getStills();
        if (stills != null && stills.length > 0) {
            for (MultipartFile still : stills) {
                try {
                    String stillFileName = fileUtil.saveMovieStill(still);
                    MoviePic moviePic = new MoviePic();
                    moviePic.setImgFilename(stillFileName);
                    moviePic.setMovie(movie); // 외래 키 설정
                    moviePicList.add(moviePic);
                } catch (IOException e) {
                    throw new RuntimeException("스틸컷 이미지 오류", e);
                }
            }
            // MoviePic 리스트를 저장
            moviePicRepository.saveAll(moviePicList);
        }

        movie.setMoviePicList(moviePicList);

        System.out.println(moviePicList.toString());
        // Movie 객체를 반환
        return movie;
    }

}
