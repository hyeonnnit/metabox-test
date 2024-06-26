package org.example.metabox.movie_scrap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.metabox.user.SessionUser;
import org.example.metabox.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MovieScrapController {
    private final MovieScrapService movieScrapService;
    private final HttpSession session;

    @PostMapping("/scrap/{id}/movie")
    public ResponseEntity<?> scrapMovie(@PathVariable Integer id) {
        movieScrapService.movieScrap(id,1);
        return ResponseEntity.ok("Movie scrap completed");
    }

    @PostMapping("/scrap/{id}")
    public String delete(@PathVariable Integer id) {
        movieScrapService.deleteMovieScrap(id, 1);
        return "redirect:/scrap/movie-list";
    }

    @GetMapping("/scrap/movie-list")
    public String scrapDetailMovie(HttpServletRequest request) {
        List<MovieScrapResponse.ScrapMovieListDTO> movieScrapList = movieScrapService.movieScrapList(1);
        request.setAttribute("movieScrapList", movieScrapList);
        return "user/mypage-detail-saw-scrap";
    }

}
