package com.example.httpnode.service;

import com.example.httpnode.dto.SecurityAttackInfo;
import com.example.httpnode.other.CustomPrinter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SecurityService {
    Map<String, SecurityAttackInfo> synchronizedMap;

    public SecurityService(){
        Map<String, SecurityAttackInfo> hashMap = new HashMap<>();
        synchronizedMap = Collections.synchronizedMap(hashMap);
    }

    public void add(String userId){
        if(synchronizedMap.containsKey(userId)){
            SecurityAttackInfo securityAttackInfo = synchronizedMap.get(userId);
            if(securityAttackInfo.getNumber() == 4){
                securityAttackInfo.setBlockDate( LocalDateTime.now().plusMinutes(5));
            }else{
                securityAttackInfo.setNumber(securityAttackInfo.getNumber()+1);
            }
        }else{
            synchronizedMap.put(userId,new SecurityAttackInfo(1,null));
        }
        CustomPrinter.printWarning(synchronizedMap.get(userId).toString());
    }

    public LocalDateTime validate(String userId){
        if(!synchronizedMap.containsKey(userId)) {
            return null;
        }else{
            SecurityAttackInfo securityAttackInfo = synchronizedMap.get(userId);
            if(securityAttackInfo.getBlockDate() == null){
                return null;
            }else{
                if(LocalDateTime.now().isAfter(securityAttackInfo.getBlockDate())){
                    synchronizedMap.remove(userId);
                    return null;
                }else{
                    return securityAttackInfo.getBlockDate();
                }
            }
        }
    }
}
