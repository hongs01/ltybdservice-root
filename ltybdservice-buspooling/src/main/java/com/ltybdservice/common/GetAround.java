package com.ltybdservice.common;
/// 根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
/// </summary>
/// <param name="lat">纬度</param>
/// <param name="lon">经度</param>
/// <param name="raidus">半径(米)</param>
/// <returns></returns>
public class GetAround {
	private static double PI = 3.14159265;
    public static double[] GetAroundByRaidus(double lat, double lon, int raidus)
    {

        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[] { minLat, minLng, maxLat, maxLng };
    }
}
