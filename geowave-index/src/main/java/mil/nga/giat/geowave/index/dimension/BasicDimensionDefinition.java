package mil.nga.giat.geowave.index.dimension;

import java.nio.ByteBuffer;

import mil.nga.giat.geowave.index.dimension.bin.BinRange;
import mil.nga.giat.geowave.index.sfc.data.NumericData;
import mil.nga.giat.geowave.index.sfc.data.NumericRange;

/**
 * The Basic Dimension Definition class defines a Space Filling Curve dimension
 * as a minimum and maximum range with values linearly interpolated within the
 * range. Values outside of the range will be clamped within the range.
 * 
 */
public class BasicDimensionDefinition implements
		NumericDimensionDefinition
{
	protected double min;
	protected double max;

	protected BasicDimensionDefinition() {}

	/**
	 * Constructor which defines and enforces the bounds of a numeric dimension
	 * definition.
	 * 
	 * @param min
	 *            the minimum bounds of the dimension
	 * @param max
	 *            the maximum bounds of the dimension
	 */
	public BasicDimensionDefinition(
			final double min,
			final double max ) {
		this.min = min;
		this.max = max;
	}

	@Override
	public double normalize(
			double value ) {
		if ((value < min) || (value > max)) {
			value = clamp(
					value,
					min,
					max);
		}

		return ((value - min) / (max - min));
	}

	@Override
	public BinRange[] getNormalizedRanges(
			final NumericData range ) {
		return new BinRange[] {
			new BinRange(
					// by default clamp to the min and max
					clamp(
							range.getMin(),
							min,
							max),
					clamp(
							range.getMax(),
							min,
							max))
		};
	}

	public NumericData getFullRange() {
		return new NumericRange(0, System.currentTimeMillis()+1);
	}
	
	private static double clamp(
			final double x,
			final double min,
			final double max ) {
		if (x < min) {
			return min;
		}
		if (x > max) {
			return max;
		}
		return x;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		final String className = getClass().getName();
		result = (prime * result) + ((className == null) ? 0 : className.hashCode());
		long temp;
		temp = Double.doubleToLongBits(max);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(min);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(
			final Object obj ) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BasicDimensionDefinition other = (BasicDimensionDefinition) obj;
		if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max)) {
			return false;
		}
		if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min)) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] toBinary() {
		final ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putDouble(min);
		buf.putDouble(max);
		return buf.array();
	}

	@Override
	public void fromBinary(
			final byte[] bytes ) {
		final ByteBuffer buf = ByteBuffer.wrap(bytes);
		min = buf.getDouble();
		max = buf.getDouble();
	}

	@Override
	public double denormalize(
			double value ) {
		if ((value < 0) || (value > 1)) {
			value = clamp(
					value,
					0,
					1);
		}

		return (value * (max - min)) + min;
	}

	@Override
	public NumericRange getDenormalizedRange(
			final BinRange range ) {
		return new NumericRange(
				range.getNormalizedMin(),
				range.getNormalizedMax());
	}

	@Override
	public int getFixedBinIdSize() {
		return 0;
	}

	@Override
	public double getRange() {
		return max - min;
	}

	@Override
	public NumericRange getBounds() {
		return new NumericRange(
				min,
				max);
	}

}
