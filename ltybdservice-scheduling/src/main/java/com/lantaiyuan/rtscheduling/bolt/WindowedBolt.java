package com.lantaiyuan.rtscheduling.bolt;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WindowedBolt extends BaseRichBolt {

	private static final long serialVersionUID = 8849434942882466073L;

	private static final Logger LOG = LoggerFactory.getLogger(WindowedBolt.class);

	private final static int DEFAULT_WINDOW_LEN_IN_SECS = 5;

	private final static int DEFAULT_WINDOW_EMIT_FREQ = 5;

	private int windowLengthInSeconds;

	private int emitFrequencyInSeconds;
    
	//存储实时客流信息的缓存对象
	protected SlidingWindowCache<Tuple> cache;

	public WindowedBolt() {
		this(DEFAULT_WINDOW_LEN_IN_SECS, DEFAULT_WINDOW_EMIT_FREQ);
	}

	public WindowedBolt(int windowLenInSecs, int emitFrequency) {
		if (windowLenInSecs % emitFrequency != 0) {
			LOG.warn(String.format("Actualwindow length(%d) isnot emitFrequency(%d)'s times"));
		}
		this.windowLengthInSeconds = windowLenInSecs;
		this.emitFrequencyInSeconds = emitFrequency;
		cache = new SlidingWindowCache<Tuple>(getSlots(this.windowLengthInSeconds, this.emitFrequencyInSeconds));
	}

	private int getSlots(int windowLenInSecs, int emitFrequency) {
		return windowLenInSecs / emitFrequency;
	}

	public void execute(Tuple tuple) {
		//判断是否系统发来的消息
		if (TupleUtils.isTick(tuple)) {
			LOG.info("====>Received tick tuple, triggering emit of current window counts");
			emitCurrentWindowCounts();
		} else {
			emitNormal(tuple);
		}
	}

	private void emitNormal(Tuple tuple) {
		cache.add(tuple);
	}

	public abstract void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector);

	public abstract void emitCurrentWindowCounts();

	public abstract void declareOutputFields(OutputFieldsDeclarer declarer);

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
		return conf;

	}

}
