package PoolGame.utils;

import PoolGame.GameObjects.Ball;
import javafx.geometry.Point2D;
import javafx.util.Pair;
public class util {
    public static void processBall2BallCollision(Ball A, Ball B){
        Point2D positionA = new Point2D(A.getXPos(),A.getYPos());
        Point2D positionB = new Point2D(B.getXPos(),B.getYPos());
        Point2D velocityA = new Point2D(A.getXVel(),A.getYVel());
        Point2D velocityB = new Point2D(B.getXVel(),B.getYVel());
        double massA      = A.getMass();
        double massB      = B.getMass();
        Pair<Point2D,Point2D> res = calculateCollision(positionA,velocityA,massA,positionB,velocityB,massB);

        Point2D velocityANew = res.getKey();
        Point2D velocityBNew = res.getValue();
        A.setXVel(velocityANew.getX());
        A.setYVel(velocityANew.getY());
        B.setXVel(velocityBNew.getX());
        B.setYVel(velocityBNew.getY());

    }
    private static Pair<Point2D, Point2D> calculateCollision(Point2D positionA, Point2D velocityA, double massA, Point2D positionB, Point2D velocityB, double massB) {

        // Find the angle of the collision - basically where is ball B relative to ball A. We aren't concerned with
        // distance here, so we reduce it to unit (1) size with normalize() - this allows for arbitrary radii
        Point2D collisionVector = positionA.subtract(positionB);
        collisionVector = collisionVector.normalize();

        // Here we determine how 'direct' or 'glancing' the collision was for each ball
        double vA = collisionVector.dotProduct(velocityA);
        double vB = collisionVector.dotProduct(velocityB);

        // If you don't detect the collision at just the right time, balls might collide again before they leave
        // each other's collision detection area, and bounce twice. This stops these secondary collisions by detecting
        // whether a ball has already begun moving away from its pair, and returns the original velocities
        if (vB <= 0 && vA >= 0) {
            return new Pair<>(velocityA, velocityB);
        }

        // This is the optimisation function described in the MassTransit link. Rather than handling the full quadratic
        // (which as we have discovered allowed for sneaky typos) this is a much simpler - and faster - way of obtaining
        // the same results.
        double optimizedP = (2.0 * (vA - vB)) / (massA + massB);

        // Now we apply that calculated function to the pair of balls to obtain their final velocities
        Point2D velAPrime = velocityA.subtract(collisionVector.multiply(optimizedP).multiply(massB));
        Point2D velBPrime = velocityB.add(collisionVector.multiply(optimizedP).multiply(massA));

        return new Pair<>(velAPrime, velBPrime);
    }
}
