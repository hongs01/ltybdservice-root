package com.ltybdservice.httputil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class LineDemand {
    private Map<String, ArrayList<String>> line = new HashMap<>();

    public void addUserDemand(UserDemand userDemand) {
        String lineName = userDemand.getOriginStation() + "-" + userDemand.getDestStation();
        if (line.get(lineName) == null) {
            ArrayList<String> array = new ArrayList<String>();
            array.add(userDemand.getUser());
            line.put(lineName, array);
        } else {
            for (String user : line.get(lineName)
                    ) {
                if (user.equals(userDemand.getUser())) {
                    return;
                }
            }
            line.get(lineName).add(userDemand.getUser());
        }
    }

    public Map<String, ArrayList<String>> getLine() {
        return line;
    }
}
