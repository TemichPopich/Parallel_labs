// program_b.cpp
#include <opencv2/opencv.hpp>
#include <omp.h>
#include <iostream>
#include <vector>
#include <chrono>
#include "utils.cpp"

using namespace cv;
using namespace std;
using namespace std::chrono;

void invertColors(Mat& src, Mat& dst) {
    dst = Scalar(255, 255, 255) - src; 
}

int main() {
    const Mat kernel = (Mat_<float>(3, 3) << 
        0, -1,  0,
       -1,  5, -1,
        0, -1,  0);

    const string programName = "Program B: Invert + Contrast\n";
    
    return Utils::processImage(kernel, programName, invertColors);
}