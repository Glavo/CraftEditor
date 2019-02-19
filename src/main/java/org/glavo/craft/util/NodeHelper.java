package org.glavo.craft.util;

import javafx.scene.Node;

public final class NodeHelper {
    private NodeHelper() {

    }

    public static void save(Node node, Object data) {
        node.getProperties().put(new Object(), data);
    }
}
