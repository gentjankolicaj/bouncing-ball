package gui;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

class BallTest {

    @Test
    void getRandY() {
        Dimension dimension = new Dimension(100, 100);
        Ball ball = new Ball(dimension);

        for (int i = 0; i < 30; i++)
            System.out.println(ball.getRandY(10));

        Assertions.assertNotEquals(ball.getRandY(10), 0);

    }
}