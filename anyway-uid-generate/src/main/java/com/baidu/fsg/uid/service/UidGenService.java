package com.baidu.fsg.uid.service;

import com.baidu.fsg.uid.UidGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/uid")
public class UidGenService {

    @Resource
    private UidGenerator uidGenerator;

    @GetMapping(value = "/generate")
    public long getUid() {
        return uidGenerator.getUID();
    }

    @GetMapping(value = "/generate/batch/{size}")
    public List<Long> getBatchUid(@PathVariable(value="size") int size) {
        List<Long> res = new ArrayList<>();
        if(size>0){
            for(int i=0;i<size;i++){
                res.add(uidGenerator.getUID());
            }
        }
        return res;
    }
}
