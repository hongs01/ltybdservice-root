package com.ltybdservice.routeorderutil;

import java.util.LinkedList;
import java.util.List;

/*
  author:  zhouzefei
  date:  2017-11-29
  effect: 将一个经纬度坐标(_loc)按照行驶顺序放入一个经纬度坐标串中(_old_lists)返还一个新的经纬度坐标串(_route_lists)
*/
public class routeOrder {
    List<double[]> _route_lists;
    public routeOrder(){
        _route_lists = new LinkedList<double[]>();
    }
    public List<double[]> get_route_lists(List <double[]> _old_lists,double[] _loc){
        boolean direction = true;
        double _begin_lon = _old_lists.get(0)[0];
        double _end_lon =  _old_lists.get(_old_lists.size()-1)[0];
        if((_end_lon - _begin_lon)<0){
            direction = false;
        }
        for(int i=0;i<_old_lists.size();i++){
            if(direction){
                if(_old_lists.get(i)[0] > _loc[0]){
                    _route_lists.add(_loc);
                    _route_lists.add(_old_lists.get(i));
                }
                else{
                    _route_lists.add(_old_lists.get(i));
                }
            }
            else {
                if(_old_lists.get(i)[0] > _loc[0]){
                    _route_lists.add(_old_lists.get(i));
                }
                else{
                    _route_lists.add(_loc);
                    _route_lists.add(_old_lists.get(i));
                }
            }
        }
        if(_old_lists.size() == _route_lists.size()){ //如果经过上面的计算两个列表的值还相等，说明待添加的节点应该是最后一个节点
            _route_lists.add(_loc);
        }
        return _route_lists;
    }
}
