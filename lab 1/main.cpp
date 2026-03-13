
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>

#include <iostream>

using namespace cv;
using namespace std;

int main() {
    const string input_path = samples::findFile("../input/photo.jpg");
    const string output_path = "../output/photo.jpg";

    Mat img = imread(input_path, IMREAD_COLOR);

    if(img.empty())
    {
        std::cout << "Could not read the image: " << input_path << std::endl;
        return 1;
    }
    
    vector<int> bias = {30, 30};

    Mat shifted_image = Mat::zeros(img.size(), img.type());
    shifted_image.setTo(Scalar(73, 38, 187));

    Rect src_roi(bias[0], bias[1], img.cols - abs(bias[0]), img.rows - abs(bias[1]));
    if (bias[0] < 0) src_roi.x = 0;
    if (bias[1] < 0) src_roi.y = 0;

    Rect dst_roi(0, 0, img.cols - abs(bias[0]), img.rows - abs(bias[1]));
    if (bias[0] > 0) dst_roi.x = bias[0];
    if (bias[1] > 0) dst_roi.y = bias[1];
    
    img(src_roi).copyTo(shifted_image(dst_roi));

    imshow("Shifted Image", shifted_image);
    waitKey(0);

    Mat kernel = Mat::ones(3, 3, CV_32F) / 9.0;

    Mat blurred_image;
    filter2D(shifted_image, blurred_image, -1, kernel);

    imwrite(output_path, blurred_image);

    return 0;
}