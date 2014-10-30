package utils;

public class EKernel {
    Long scale;

    public EKernel(Long scale) {
        this.scale = scale;
    }

    public double kernel(double u) {
        return Math.abs(u /= scale) <= 1 ? .75 * (1 - u * u) / scale : 0;
    }

}
