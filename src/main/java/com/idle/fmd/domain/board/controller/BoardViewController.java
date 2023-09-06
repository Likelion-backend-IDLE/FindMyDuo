package com.idle.fmd.domain.board.controller;

import com.idle.fmd.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class BoardViewController {

    private final BoardService boardService;


    @GetMapping("/view")
    public String board(){
        return "board/board-list";
    }

    @GetMapping("/bookmark")
    public String bookmark(){
        return "board/board-bookmark";
    }

}
