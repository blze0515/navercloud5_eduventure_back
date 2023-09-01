package com.bit.eduventure.lecture.controller;

import com.bit.eduventure.ES1_User.Service.UserService;
import com.bit.eduventure.jwt.JwtTokenProvider;
import com.bit.eduventure.lecture.dto.ChatMessage;
import com.bit.eduventure.lecture.entity.LecUser;
import com.bit.eduventure.lecture.service.LecUserService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RequestMapping("/liveChat")
@Controller
//@RestController
public class LectureChatController {

    private final SimpMessagingTemplate template;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final LecUserService lecUserService;


    @GetMapping("/abc")
    public String chat() {
        return "/chat/chat";
    }

    @MessageMapping("/sendMsg/{lectureId}")
    @SendTo("/topic/lecture/{lectureId}")
    public String sendMessage(@Header("Authorization") String token,
                              @Payload String chatMessage,
                              @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            Map<String, Object> chatMsgMap = gson.fromJson(chatMessage, Map.class);

            String content = (String) chatMsgMap.get("content");

            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);
            String userName = userService.findByUserId(userId).getUserName();

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(content)
                    .sender(userName)
                    .build();

            return gson.toJson(returnMsg);

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @MessageMapping("/sendMsg/{lectureId}/addUser")
    @SendTo("/topic/lecture/{lectureId}") //보내는 곳은 똑같이
    public String addUser(@Header("Authorization") String token,
                          @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);
            String userName = userService.findByUserId(userId).getUserName();

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(userName + "님이 입장하였습니다.")
                    .build();

            //DB에 강의에 들어온 유저 저장
            lecUserService.enterLecUser(lectureId, userName);

            List<LecUser> lecUserList = lecUserService.lecUserList(lectureId);

            if (!lecUserList.isEmpty()) {
                List<String> userList = lecUserList.stream()
                        .map(LecUser::getUserName)
                        .collect(Collectors.toList());
                returnMsg.setUserList(userList);
            }

            return gson.toJson(returnMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @MessageMapping("/sendMsg/{lectureId}/leave")
    @SendTo("/topic/lecture/{lectureId}") //보내는 곳은 똑같이
    public String leaveUser(@Header("Authorization") String token,
                            @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);
            String userName = userService.findByUserId(userId).getUserName();

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(userName + "님이 나가셨습니다.")
                    .build();

            //DB에 강의에 나간 유저 삭제
            lecUserService.leaveLecUser(lectureId, userName);

            List<LecUser> lecUserList = lecUserService.lecUserList(lectureId);

            if (!lecUserList.isEmpty()) {
                List<String> userList = lecUserList.stream()
                        .map(LecUser::getUserName)
                        .collect(Collectors.toList());
                returnMsg.setUserList(userList);
            }

            return gson.toJson(returnMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
