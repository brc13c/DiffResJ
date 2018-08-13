package mathlibrary.number;
import markers.Immutable;
import java.util.Arrays;

/**
 * This represents an integer in as many bits as are desired.
 * @author Benjamin
 */
public class ArbitraryInteger extends Number implements Immutable{
	
	/**
	 * Class version.
	 */
	private static final long serialVersionUID = 9115299221642487628L;

	/**
	 * The minimum valid bit index.
	 */
	protected static final int MININDEX = 0;
	
	/**
	 * The specified bits in this integer. (Index 0 is least significant.)
	 */
	protected boolean[] bits;
	
	/**
	 * This directly sets the bits in this integer.
	 * @param bitValues The bits for this integer.
	 */
	public ArbitraryInteger(final boolean[] bitValues) {
		super();
		bits = Arrays.copyOf(bitValues, bitValues.length);
	}
	
	/**
	 * This creates an arbitrary representation of the given value.
	 * @param toCopy The value to represent.
	 */
	public ArbitraryInteger(final long toCopy){
		super();
		bits = new boolean[64];
		for(int i = 0; i<bits.length; i++){
			bits[i] = ((0x01 & (toCopy >> i)) == 1 ? true : false);
		}
	}
	
	/**
	 * This creates an arbitrary representation of the given value.
	 * @param toCopy The value to represent.
	 */
	public ArbitraryInteger(final int toCopy){
		super();
		bits = new boolean[32];
		for(int i = 0; i<bits.length; i++){
			bits[i] = ((0x01 & (toCopy >> i)) == 1 ? true : false);
		}
	}
	
	/**
	 * This creates an arbitrary representation of the given value.
	 * @param toCopy The value to represent.
	 */
	public ArbitraryInteger(final short toCopy){
		super();
		bits = new boolean[16];
		for(int i = 0; i<bits.length; i++){
			bits[i] = ((0x01 & (toCopy >> i)) == 1 ? true : false);
		}
	}
	
	/**
	 * This creates an arbitrary representation of the given value.
	 * @param toCopy The value to represent.
	 */
	public ArbitraryInteger(final byte toCopy){
		super();
		bits = new boolean[8];
		for(int i = 0; i<bits.length; i++){
			bits[i] = ((0x01 & (toCopy >> i)) == 1 ? true : false);
		}
	}
	
	/**
	 * This returns a bit from this integer.
	 * @param bitNum The index of the bit to get. Index 0 is the least significant bit.
	 * @return The requested bit in this integer. If this integer's bit depth is less than bitNum, this integer is sign extended.
	 */
	public boolean getBit(final int bitNum){
		if(bitNum < MININDEX){
			throw new ArrayIndexOutOfBoundsException("Can only get a positive index bit.");
		}
		if(bitNum < bits.length){
			return bits[bitNum];
		}
		else{
			return bits[bits.length - 1];
		}
	}
	
	/**
	 * This returns the minimum number of bits required to completely represent this integer.
	 * @return The number of bits required to represent this integer.
	 */
	public int bitDepth(){
		return bits.length;
	}
	
	@Override
	public double doubleValue() {
		return (double)longValue();
	}
	
	@Override
	public float floatValue() {
		return (float)doubleValue();
	}
	
	@Override
	public int intValue() {
		return (int)longValue();
	}
	
	@Override
	public long longValue() {
		long toRet = 0;
		for(int i = 0; i<64; i++){
			toRet = toRet + ((getBit(i) ? 1l : 0l) << i);
		}
		return toRet;
	}
}