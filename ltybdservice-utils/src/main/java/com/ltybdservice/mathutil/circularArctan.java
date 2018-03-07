package com.ltybdservice.mathutil;

public class circularArctan {
    public double getCirArctan(double[] _loc_1,double[] _loc_2){
        double _diff_x = _loc_2[0] - _loc_1[0];
        double _diff_y = _loc_2[1] - _loc_1[1];
        double atan;
        if(_diff_x != 0){
            atan = Math.toDegrees(Math.atan(_diff_y/_diff_x));
        }
        else{
            atan = _diff_y>0?90:180;
            return atan;
        }
        //将角度转换成360度体系
        if(_diff_x > 0 && _diff_y > 0){ //第一象限
            atan = atan;
        }
        else if(_diff_x < 0 && _diff_y > 0){ //第二象限
            atan = 180 - Math.abs(atan);
        }
        else if(_diff_x < 0 && _diff_y < 0){ //第三象限
            atan = atan + 180;
        }
        else if(_diff_x > 0 && _diff_y < 0 ){
            atan = 360 - Math.abs(atan);
        }
        return atan;
    }
}
