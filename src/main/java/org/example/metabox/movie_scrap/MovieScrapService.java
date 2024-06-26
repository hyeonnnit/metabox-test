package org.example.metabox.movie_scrap;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.metabox._core.errors.exception.Exception401;
import org.example.metabox._core.errors.exception.Exception404;
import org.example.metabox.movie.Movie;
import org.example.metabox.movie.MovieRepository;
import org.example.metabox.user.User;
import org.example.metabox.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieScrapService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieScrapRepository movieScrapRepository;

    @Transactional
    public MovieScrapResponse.ScrapDTO movieScrap(Integer movieId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new Exception401("존재하지 않는 영화입니다!."));
        MovieScrapRequest.ScrapMovieDTO movieScrapDTO = new MovieScrapRequest.ScrapMovieDTO(user,movie);
        MovieScrap movieScrap = movieScrapRepository.save(movieScrapDTO.toEntity());
        return new MovieScrapResponse.ScrapDTO(movieScrap);
    }

    @Transactional
    public void deleteMovieScrap(Integer movieId, Integer userId) {
        User user = userRepository.findById(movieId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));
        Movie movie = movieRepository.findById(userId)
                .orElseThrow(() -> new Exception401("존재하지 않는 영화입니다!."));
        MovieScrap movieScrap = movieScrapRepository.findByScrapAndUser(user.getId(), movie.getId());
        movieScrapRepository.delete(movieScrap);
    }

    public List<MovieScrapResponse.ScrapMovieListDTO> movieScrapList(Integer userId) {
        List<MovieScrap> movieScrapList = movieScrapRepository.findByUserAndMovie(userId);
        return movieScrapList.stream().map(MovieScrapResponse.ScrapMovieListDTO::new).toList();
    }
}
