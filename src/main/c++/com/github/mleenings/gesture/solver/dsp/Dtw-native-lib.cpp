#include "aquila-dsp/Dtw-native-lib.h"
#include <jni.h>
#include "../../../../../../include/aquila-dsp/aquila/ml/Dtw.h"
#include "../../../../../../include/aquila-dsp/aquila/global.h"
#include "../../../../../../include/aquila-dsp/aquila/functions.h"
#include <vector>
#include <iostream>

using namespace Aquila;

static int rows;
static int columns;

double** convertJobjectArrayToCArray(JNIEnv *env, jobjectArray jArray){
  int len1 = env -> GetArrayLength(jArray);
  rows = len1;
  jdoubleArray dim=  (jdoubleArray)env->GetObjectArrayElement(jArray, 0);
  int len2 = env -> GetArrayLength(dim);
  columns = len2;
  double **cArray;
  cArray = new double*[len1];
  for(int i=0; i<len1; ++i){
     jdoubleArray oneDim= (jdoubleArray)env->GetObjectArrayElement(jArray, i);
     jdouble *element=env->GetDoubleArrayElements(oneDim, 0);
     cArray[i] = new double[len2];
     for(int j=0; j<len2; ++j) {
        cArray[i][j]= element[j];
     }
  }
    return cArray;
}

std::vector<std::vector<double>> convertArrayToVector(double** ary){
    std::vector<std::vector<double> > vec;
    for(int c = 0; c<columns; c++){
        double aryCol [rows]  = {};
        for(int r = 0; r<rows; r++){
            aryCol[r] = ary[r][c];
        }
        std::vector<double> vecRow (aryCol, aryCol + sizeof aryCol / sizeof aryCol[0]);
        vec.push_back(vecRow);
    }

    // test print
//    std::cout << "test print vector:" << std::endl;
//    for (int i = 0; i < vec.size(); i++){
//        std::cout << "vector no:" << i << std::endl;
//        for (int j = 0; j < vec[i].size(); j++){
//            std::cout << vec[i][j];
//        }
//        std::cout<<std::endl;
//    }
    return vec;
}

DtwDataType convertJobjectArrayToDtwDataType(JNIEnv *env, jobjectArray jArray){
    double ** aryC =  convertJobjectArrayToCArray(env, jArray);
    std::vector<std::vector<double>> vec = convertArrayToVector(aryC);
    Aquila::DtwDataType typeVec = vec;
    return typeVec;
}

JNIEXPORT jdouble Java_de_fhaachen_navi_gesture_solver_aquila_Dtw_getDistance(JNIEnv *env, jobject obj, jobjectArray fromAryJ, jobjectArray toAryJ) {
    Aquila::DtwDataType from, to;
    from = convertJobjectArrayToDtwDataType(env, fromAryJ);
    to = convertJobjectArrayToDtwDataType(env, toAryJ);
    Aquila::Dtw dtw;
    dtw.getDistance(from, to);
}

JNIEXPORT jstring Java_de_fhaachen_navi_gesture_solver_aquila_Dtw_sayHello(JNIEnv *env, jobject obj) {
    std::cout << "Hello in C++" << std::endl;
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

