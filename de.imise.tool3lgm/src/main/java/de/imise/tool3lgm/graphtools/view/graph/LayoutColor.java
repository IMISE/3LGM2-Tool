package de.imise.tool3lgm.graphtools.view.graph;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Color;

/**
 * @author AXS (26.02.2021)
 */
public enum LayoutColor {
    WHITE {
        @Override
        public Color awtColor() {
            return Color.white;
        }
    },
    YELLOW {
        @Override
        public Color awtColor() {
            return Color.yellow;
        }
    },
    GREEN {
        @Override
        public Color awtColor() {
            return Color.green;
        }
    },
    BLUE {
        @Override
        public Color awtColor() {
            return new Color(100, 100, 255);
        }
    },
    GRAY {
        @Override
        public Color awtColor() {
            return Color.lightGray;
        }
    },
    RED {
        @Override
        public Color awtColor() {
            return new Color(255, 100, 100);
        }
    },
    ORANGE {
        @Override
        public Color awtColor() {
            return Color.orange;
        }
    },
    BLACK {
        @Override
        public Color awtColor() {
            return Color.black;
        }
    },
    LIGHTRED {
        @Override
        public Color awtColor() {
            return new Color(255, 153, 102);
        }
    },
    LIGHTGREEN {
        @Override
        public Color awtColor() {
            return new Color(204, 255, 204);
        }
    },
    LIGHTPURPLE {
        @Override
        public Color awtColor() {
            return new Color(229, 203, 255);
        }
    },
    LIGHTBLUE {
        @Override
        public Color awtColor() {
            return new Color(0, 204, 255);
        }
    };

    /**
     * @return
     */
    public abstract Color awtColor();

    @Override
    public String toString() {
        return getResString(name());
    }
}