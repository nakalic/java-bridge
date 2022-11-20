package bridge.domain;

import java.util.ArrayList;
import java.util.List;

public class Bridge {

    List<String> bridge;

    public Bridge(List<String> bridge) {
        this.bridge = bridge;
    }

    public List<String> getBridge() {
        return bridge;
    }

    @Override
    public String toString() {
        return bridge.toString();
    }
}
