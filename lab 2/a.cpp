// program_a.cpp
#include <opencv2/opencv.hpp>
#include <omp.h>
#include <iostream>
#include <vector>
#include <chrono>
#include "utils.cpp"

using namespace cv;
using namespace std;
using namespace std::chrono;

void shiftPixels(Mat& src, Mat& dst) {
    dst.create(src.size(), src.type());

    const int dx = 50, dy = 30;
    const Scalar border_color(73, 38, 187);

    dst.setTo(border_color);

    int w = src.cols - abs(dx);
    int h = src.rows - abs(dy);

    int src_x = (dx > 0) ? 0 : -dx;
    int src_y = (dy > 0) ? 0 : -dy;
    int dst_x = (dx > 0) ? dx : 0;
    int dst_y = (dy > 0) ? dy : 0;

    if (w <= 0 || h <= 0) return;

    Rect src_roi(src_x, src_y, w, h);
    Rect dst_roi(dst_x, dst_y, w, h);

    src(src_roi).copyTo(dst(dst_roi));
}

int main() {
    const Mat kernel = (Mat_<float>(5, 5) << 
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1) / 25.0;

    const string programName = "Program A: Shift + Blur";

    return Utils::processImage(kernel, programName, shiftPixels);
}
