package com.idle.fmd.domain.board.controller;

import com.idle.fmd.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class BoardViewController {

    @GetMapping("/view")
    public String board(){
        return "board/board-list";
    }

    @GetMapping("/bookmark")
    public String bookmark(){
        return "board/board-bookmark";
    }

    // 글 작성 폼으로 이동
    @GetMapping("/form/write")
    public String writeForm() {
        return "/board/board-write-form";
    }

    // 글 수정 폼으로 이동
    @GetMapping("/form/{boardId}")
    public String updateForm() {
        return "/board/board-update-form";
    }
}
