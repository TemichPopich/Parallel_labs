#include <opencv2/opencv.hpp>
#include <omp.h>
#include <iostream>
#include <vector>
#include <chrono>

using namespace cv;
using namespace std;
using namespace std::chrono;

class Utils {
    public:
        inline static const vector<int> thread_counts = {2, 4, 6, 8, 10, 12, 14, 16};

        inline static const vector<string> output_paths = {
            "../output/1024x768_shift_blur.png",
            "../output/1280x960_shift_blur.png",
            "../output/2048x1536_shift_blur.png"
        };

        inline static const vector<string> image_paths = {
            "../input/1024x768.png",
            "../input/1280x960.png",
            "../input/2048x1536.png"
        }; 

        static int processImage(const Mat& kernel, String programName, void (*func)(Mat& src, Mat& dst)) {
            cout << programName << "\n";
            cout << "Threads\t1024x768\t1280x960\t2048x1536\n";

            for (int n_threads : thread_counts) {
                omp_set_num_threads(n_threads);
                vector<double> times(3);

                for (int img_idx = 0; img_idx < 3; img_idx++) {
                    double total_time = 0.0;

                    for (int run = 0; run < 3; run++) {
                        Mat img = imread(Utils::image_paths[img_idx], IMREAD_COLOR);
                        if (img.empty()) {
                            cerr << "Could not load: " << Utils::image_paths[img_idx] << endl;
                            return -1;
                        }

                        Mat modified, result;

                        auto start = high_resolution_clock::now();

                        func(img, modified);

                        convolute(modified, result, kernel);

                        auto end = high_resolution_clock::now();
                        duration<double, milli> ms = end - start;
                        total_time += ms.count();

                        if (n_threads == 16 && run == 2) {
                            imwrite(Utils::output_paths[img_idx], result);
                        }
                    }

                    times[img_idx] = total_time / 3.0; 
                }

                printf("%d\t%.2f\t\t%.2f\t\t%.2f\n", 
                    n_threads, times[0], times[1], times[2]);
            }

            return 0;
        }

    private:
        static void convolute(const Mat& src, Mat& dst, const Mat& kernel) {
            dst = Mat::zeros(src.size(), src.type());

            int kh = kernel.rows / 2;
            int kw = kernel.cols / 2;

            #pragma omp parallel for collapse(2)
            for (int y = 0; y < src.rows; y++) {
                for (int x = 0; x < src.cols; x++) {
                    double sum_b = 0.0, sum_g = 0.0, sum_r = 0.0;

                        for (int ky = 0; ky < kernel.rows; ky++) {
                            for (int kx = 0; kx < kernel.cols; kx++) {
                                int iy = y + ky - kh;
                                int ix = x + kx - kw;

                                double k_val = kernel.at<float>(ky, kx);

                                if (iy >= 0 && iy < src.rows && ix >= 0 && ix < src.cols) {
                                    Vec3b pixel = src.at<Vec3b>(iy, ix);
                                    sum_b += k_val * pixel[0];
                                    sum_g += k_val * pixel[1];
                                    sum_r += k_val * pixel[2];
                                }
                            }
                    }

                    dst.at<Vec3b>(y, x) = Vec3b(
                        saturate_cast<uchar>(sum_b),
                        saturate_cast<uchar>(sum_g),
                        saturate_cast<uchar>(sum_r)
                    );
                }
            }
        }
};
