package com.lty.common.util;

import java.math.BigDecimal;

/***
 * 
* <p>Title: Coordinate</p>
* <p>Description: 经纬度转换工具类</p>
* <p>Company: lty</p>
* @author liuhao
* @date 2016年12月13日 下午6:38:38
* version 1.0
 */
public class Coordinate {
		private int degree;				//度
		private int minute;				//分
		private int second;				//秒
		private BigDecimal exactValue;	//以"度"的形式获得固定精度和小数位数的值。
		private double doubleValue;		//以"度"的形式获得双精度浮点值。
		
		/**
		 * 构造函数
		 * @param degree
		 */
		public Coordinate(double degree)
		{
			doubleValue = degree;
			exactValue = new BigDecimal(String.format("%8.5f",doubleValue).trim());
			
			this.degree = (int)doubleValue;
			this.minute = (int)((doubleValue - this.degree) * 60); 
			this.second = (int)((((doubleValue - this.degree) * 60) - minute) * 60);
		}
		/**
		 * 构造函数
		 * @param degree
		 */
		public Coordinate( int degree, int minute, int second )
		{
			this.degree = degree;
			this.minute = minute;
			this.second = second;
			
			doubleValue = degree + ((double)minute / 60) + ((double)second / 60 / 60);
			exactValue = new BigDecimal( String.format("%8.5f",doubleValue).trim() );
		}
		public Coordinate( int degree, int minute )
		{
			this.degree = degree;
			this.minute = minute/60;
			
			doubleValue = degree + ((double)minute/60);
			exactValue = new BigDecimal( String.format("%8.5f",doubleValue).trim() );
		}		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return this.degree + "°" + this.minute + "'" + this.second + "\"";
		}
		
		public String toHexString()
		{
			return "度：" + Integer.toHexString(degree) + ",分：" + Integer.toHexString(minute) + ",秒:"+Integer.toHexString(second);
		}
		
		/**
		 * 获得度。
		 */
		public int getDegree()
		{
			return degree;
		}
			
		/**
		 * 获得分。
		 */
		public int getMinute()
		{
			return minute;
		}
			
		/**
		 * 获得秒。
		 */
		public int getSecond()
		{
			return second;
		}
			
		/**
		 * 以"度"的形式获得固定精度和小数位数的值。
		 */
		public BigDecimal getExactValue()
		{
			return exactValue; 
		}
			
		/**
		 * 以"度"的形式获得双精度浮点值。
		 */
		public double getDoubleValue()
		{
			return doubleValue;
		}
		
		public String getDbStr()
		{
			return degree + "-" + minute + "-" + second;
		}
		
		public static void main(String[] args) {
			double d = 31.567861;
			new Coordinate(d);
		}
}
