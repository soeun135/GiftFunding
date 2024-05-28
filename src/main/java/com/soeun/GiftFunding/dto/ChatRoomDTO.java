package com.soeun.GiftFunding.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDTO {

    private String roomId;
    private String name;

    public static ChatRoomDTO create(String name){
        ChatRoomDTO room = new ChatRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }
}