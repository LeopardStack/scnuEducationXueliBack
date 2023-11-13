package com.scnujxjy.backendpoint.model.vo.basic;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class OnlineCount implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String, Integer> roleCounts;
    private int totalOnlineCount;

    public OnlineCount() {
        this.roleCounts = new HashMap<>();
        this.totalOnlineCount = 0;
    }

    public void updateCount(String roleName, boolean increment) {
        int count = roleCounts.getOrDefault(roleName, 0);
        count = increment ? count + 1 : Math.max(count - 1, 0);
        roleCounts.put(roleName, count);

        totalOnlineCount = increment ? totalOnlineCount + 1 : Math.max(totalOnlineCount - 1, 0);
    }

    // ...其他必要的getter和setter...
}
