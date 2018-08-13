package mathlibrary.number;
import markers.Immutable;
import java.util.Arrays;

/**
 * This represents an IEEE compatible arbitrary bit width floating point number.
 * @author Benjamin
 */
@SuppressWarnings("PMD.GodClass") //damn IEEE
public class ArbitraryFloat extends Number implements Immutable{
	
	/**
	 * Version number.
	 */
	private static final long serialVersionUID = -3898049475226317532L;

	/**
	 * The number of bits in the exponent field of an IEEE 64 bit float.
	 */
	public static final int DOUBLEEXPONENTBITS = 11;
	
	/**
	 * The number of bits in the mantissa field of an IEEE 64 bit float.
	 */
	public static final int DOUBLEMANTISSABITS = 52;
	
	/**
	 * The number of bits in the exponent field of an IEEE 32 bit float.
	 */
	public static final int SINGLEEXPONENTBITS = 8;

	/**
	 * The number of bits in the mantissa field of an IEEE 32 bit float.
	 */
	public static final int SINGLEMANTISSABITS = 23;
	
	/**
	 * The sign bit.
	 */
	protected boolean signBit;
	
	/**
	 * The bits used for the exponent (bit 0 is least significant).
	 */
	protected boolean[] exponentBits;
	
	/**
	 * The bits used for the mantissa (bit 0 is least significant, the leading 1 is not included).
	 */
	protected boolean[] mantissaBits;
	
	/**
	 * This creates an arbitrary bit depth float with the given values.
	 * @param sign The sign (false = positive, true = negative).
	 * @param exponent The bits used for the exponent (bit 0 is least significant).
	 * @param mantissa The bits used for the mantissa (bit 0 is least significant, the leading 1 is not included).
	 */
	public ArbitraryFloat(final boolean sign, final boolean[] exponent, final boolean[] mantissa){
		super();
		signBit = sign;
		exponentBits = Arrays.copyOf(exponent, exponent.length);
		mantissaBits = Arrays.copyOf(mantissa, mantissa.length);
	}
	
	/**
	 * This copies the data from a double.
	 * @param toCopy The double to copy the data of.
	 */
	public ArbitraryFloat(final double toCopy){
		super();
		final long asBits = Double.doubleToRawLongBits(toCopy);
		signBit = (0x01 & (asBits >> 63)) == 1;
		exponentBits = new boolean[DOUBLEEXPONENTBITS];
		for(int i = 0; i<exponentBits.length; i++){
			exponentBits[i] = (0x01 & (asBits >> (52 + i))) == 1;
		}
		mantissaBits = new boolean[DOUBLEMANTISSABITS];
		for(int i = 0; i<mantissaBits.length; i++){
			mantissaBits[i] = (0x01 & (asBits >> i)) == 1;
		}
	}
	
	/**
	 * This copies the data from a float.
	 * @param toCopy The float to copy the data of.
	 */
	public ArbitraryFloat(final float toCopy){
		super();
		final int asBits = Float.floatToRawIntBits(toCopy);
		signBit = (0x01 & (asBits >> 31)) == 1;
		exponentBits = new boolean[SINGLEEXPONENTBITS];
		for(int i = 0; i<exponentBits.length; i++){
			exponentBits[i] = (0x01 & (asBits >> (23 + i))) == 1;
		}
		mantissaBits = new boolean[SINGLEMANTISSABITS];
		for(int i = 0; i<mantissaBits.length; i++){
			mantissaBits[i] = (0x01 & (asBits >> i)) == 1;
		}
	}
	
	/**
	 * This will create a new float with the given bit depths.
	 * @param numExponentBits The number of bits to use for the exponent.
	 * @param numMantissaBits The number of bits to use for the mantissa.
	 * @return The new float.
	 */
	public ArbitraryFloat changeBitDepth(final int numExponentBits, final int numMantissaBits){
		if(numExponentBits == exponentBits.length && numMantissaBits == mantissaBits.length){
			return this;
		}
		else if(numExponentBits == exponentBits.length){
			//just change the mantissa bits
			return changeMantissaBitDepth(numMantissaBits);
		}
		else{
			//change the exponent bits
			return changeExponentBitDepth(numExponentBits).changeBitDepth(numExponentBits, numMantissaBits);
		}
	}
	
	/**
	 * This will create a new float with a different bit depth for the exponent.
	 * @param numExponentBits The number of bits for the exponent.
	 * @return The new float.
	 */
	protected ArbitraryFloat changeExponentBitDepth(final int numExponentBits){
		if(numExponentBits == exponentBits.length){
			return this;
		}
		
		//get information on special cases
		boolean exponentAllZeros = true;
		boolean exponentAllOnes = true;
		for(int i = 0; i<exponentBits.length; i++){
			exponentAllZeros = exponentAllZeros && !exponentBits[i];
			exponentAllOnes = exponentAllOnes && exponentBits[i];
		}
		boolean mantissaAllZeros = true;
		for(int i = 0; i<mantissaBits.length; i++){
			mantissaAllZeros = mantissaAllZeros && !mantissaBits[i];
		}
		//handle zero, inf and NaN
		if(exponentAllZeros && mantissaAllZeros){
			return new ArbitraryFloat(signBit, new boolean[numExponentBits], mantissaBits);
		}
		if(exponentAllOnes){
			final boolean[] newExponentBits = new boolean[numExponentBits];
			Arrays.fill(newExponentBits, true);
			return new ArbitraryFloat(signBit, newExponentBits, mantissaBits);
		}
		
		//get the twos complement representation of the exponent
		boolean[] exponentShift = new boolean[exponentBits.length];
		exponentShift[0] = true;
		exponentShift[exponentShift.length-1] = true;
		final boolean[] exponentTwosComplement = add(exponentShift, exponentBits);
		
		//what happens next depends on whether there is expansion or contraction
		if(numExponentBits > exponentBits.length){
			return expandExponent(numExponentBits, exponentAllZeros, exponentTwosComplement);
		}
		else{
			return contractExponent(numExponentBits, exponentAllZeros, exponentTwosComplement);
		}
	}
	
	/**
	 * This will create a new float with a larger exponent bit depth.
	 * @param numExponentBits The new exponent bit depth.
	 * @param exponentAllZeros Whether the current exponent is all zeroes.
	 * @param exponentTwosComplement The twos complement representation of the current exponent.
	 * @return The new float.
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") //only done once, on return
	protected ArbitraryFloat expandExponent(final int numExponentBits, final boolean exponentAllZeros, final boolean[] exponentTwosComplement){
		if(exponentAllZeros){
			//if it's a sub-normal number, it becomes a normalized number
			int firstOne = 0;
			for(int i = mantissaBits.length-1; i>=0; i--){
				if(mantissaBits[i]){
					firstOne = i;
					break;
				}
			}
			//keep moving the mantissa bits to the left, until either the exponent underflows, or the leading one is no longer present
			boolean[] curExponentTwosComplement = signExtend(exponentTwosComplement, numExponentBits);
			final boolean[] negativeOne = new boolean[numExponentBits]; Arrays.fill(negativeOne, true);
			boolean[] prevExponentTwosComplement = curExponentTwosComplement;
			boolean[] curMantissa = Arrays.copyOf(mantissaBits, mantissaBits.length);
			boolean[] prevMantissa = curMantissa;
			for(int i = firstOne - 1; i<numExponentBits; i++){
				prevExponentTwosComplement = curExponentTwosComplement;
				prevMantissa = curMantissa;
				curExponentTwosComplement = add(negativeOne, curExponentTwosComplement);
				curMantissa = leftShift(curMantissa);
				if(!curExponentTwosComplement[numExponentBits-1]){
					//underflow, return previous
					final boolean[] newExponentShift = new boolean[numExponentBits];
					Arrays.fill(newExponentShift, 0, numExponentBits-1, true);
					final boolean[] newExponentBits = add(prevExponentTwosComplement, newExponentShift);
					return new ArbitraryFloat(signBit, newExponentBits, prevMantissa);
				}
			}
			final boolean[] newExponentShift = new boolean[numExponentBits];
			Arrays.fill(newExponentShift, 0, numExponentBits-1, true);
			final boolean[] newExponentBits = add(curExponentTwosComplement, newExponentShift);
			return new ArbitraryFloat(signBit, newExponentBits, curMantissa);
		}
		else{
			//otherwise, sign extend
			final boolean[] newExponentTwosComplement = signExtend(exponentTwosComplement, numExponentBits);
			final boolean[] newExponentShift = new boolean[numExponentBits];
			Arrays.fill(newExponentShift, 0, numExponentBits-1, true);
			final boolean[] newExponentBits = add(newExponentTwosComplement, newExponentShift);
			return new ArbitraryFloat(signBit, newExponentBits, mantissaBits);
		}
	}
	
	/**
	 * This will create a new float with a smaller exponent bit depth.
	 * @param numExponentBits The new exponent bit depth.
	 * @param exponentAllZeros Whether the current exponent is all zeroes.
	 * @param exponentTwosComplement The twos complement representation of the current exponent.
	 * @return The new float.
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") //only done once, on return
	protected ArbitraryFloat contractExponent(final int numExponentBits, final boolean exponentAllZeros, final boolean[] exponentTwosComplement){
		if(exponentAllZeros){
			//if it's a sub-normal number, it becomes zero
			return new ArbitraryFloat(signBit, new boolean[numExponentBits], new boolean[mantissaBits.length]);
		}
		else{
			if(exponentTwosComplement[exponentTwosComplement.length-1]){
				//check for underflow
				for(int i = numExponentBits-1; i<exponentTwosComplement.length; i++){
					if(!exponentTwosComplement[i]){
						return new ArbitraryFloat(signBit, new boolean[numExponentBits], new boolean[mantissaBits.length]);
					}
				}
			}
			else{
				//check for overflow
				for(int i = numExponentBits-1; i<exponentTwosComplement.length; i++){
					if(exponentTwosComplement[i]){
						final boolean[] newExponentBits = new boolean[numExponentBits];
						Arrays.fill(newExponentBits, true);
						return new ArbitraryFloat(signBit, newExponentBits, new boolean[mantissaBits.length]);
					}
				}
			}
			//simple conversion
			final boolean[] newExponentTwosComplement = signExtend(exponentTwosComplement, numExponentBits);
			final boolean[] newExponentShift = new boolean[numExponentBits];
			Arrays.fill(newExponentShift, 0, numExponentBits-1, true);
			final boolean[] newExponentBits = add(newExponentTwosComplement, newExponentShift);
			return new ArbitraryFloat(signBit, newExponentBits, mantissaBits);
		}
	}
	
	/**
	 * This will create a new float with a different mantissa bit depth.
	 * @param numMantissaBits The new bit depth for the mantissa.
	 * @return The new float.
	 */
	protected ArbitraryFloat changeMantissaBitDepth(final int numMantissaBits){
		final boolean[] newMantissaBits = new boolean[numMantissaBits];
		if(numMantissaBits >= mantissaBits.length){
			//copy mantissa bits to the new bits, and zero remainder
			Arrays.fill(newMantissaBits, 0, numMantissaBits - mantissaBits.length, false);
			System.arraycopy(mantissaBits, 0, newMantissaBits, (numMantissaBits - mantissaBits.length), mantissaBits.length);
		}
		else{
			//copy some of the mantissa bits to the new bits
			System.arraycopy(mantissaBits, mantissaBits.length - numMantissaBits, newMantissaBits, 0, numMantissaBits);
			//check for nan
			boolean exponentAllOne = true;
			boolean mantissaAllZero = true;
			for(int i = 0; i<exponentBits.length; i++){
				exponentAllOne = exponentAllOne && exponentBits[i];
			}
			for(int i = 0; i<mantissaBits.length; i++){
				mantissaAllZero = mantissaAllZero && !mantissaBits[i];
			}
			if(exponentAllOne && !mantissaAllZero){
				//check that the new mantissa is nan
				boolean newMantissaAllZero = true;
				for(int i = 0; i<newMantissaBits.length; i++){
					newMantissaAllZero = newMantissaAllZero && !newMantissaBits[i];
				}
				if(newMantissaAllZero){
					//if new mantissa is 0, change it to all ones
					Arrays.fill(newMantissaBits, true);
				}
			}
		}
		return new ArbitraryFloat(signBit, exponentBits, newMantissaBits);
	}
	
	/**
	 * This gets this floats sign bit.
	 * @return The sign bit of this float.
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName") //fits the form of the other methods
	public boolean getSignBit(){
		return signBit;
	}
	
	/**
	 * This gets one of the exponents bits.
	 * @param bit The bit to get.
	 * @return The exponent bit.
	 * @throws ArrayIndexOutOfBoundsException If bit is less than zero or greater than the maximum bit index.
	 */
	public boolean getExponentBit(final int bit){
		return exponentBits[bit];
	}
	
	/**
	 * This returns the number of bits the exponent takes up.
	 * @return The number of bits the exponent takes up.
	 */
	public int exponentBitDepth(){
		return exponentBits.length;
	}
	
	/**
	 * This gets one of the mantissa bits.
	 * @param bit The bit to get.
	 * @return The mantissa bit.
	 * @throws ArrayIndexOutOfBoundsException If bit is less than zero or greater than the maximum bit index.
	 */
	public boolean getMantissaBit(final int bit){
		return mantissaBits[bit];
	}
	
	/**
	 * This returns the number of bits the mantissa takes up.
	 * @return The number of bits the mantissa takes up.
	 */
	public int mantissaBitDepth(){
		return exponentBits.length;
	}
	
	@Override
	public double doubleValue() {
		if(exponentBits.length == DOUBLEEXPONENTBITS && mantissaBits.length == DOUBLEMANTISSABITS){
			final long signLong = signBit ? 1l : 0l;
			long exponentLong = 0;
			for(int i = 0; i<DOUBLEEXPONENTBITS; i++){
				exponentLong = exponentLong + ((exponentBits[i] ? 1l : 0l) << i);
			}
			long mantissaLong = 0;
			for(int i = 0; i<DOUBLEMANTISSABITS; i++){
				mantissaLong = mantissaLong + ((mantissaBits[i] ? 1l : 0l) << i);
			}
			final long doubleLong = (signLong << 63) + (exponentLong << 52) + mantissaLong;
			return Double.longBitsToDouble(doubleLong);
		}
		else{
			return changeBitDepth(DOUBLEEXPONENTBITS, DOUBLEMANTISSABITS).doubleValue();
		}
	}

	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	@Override
	public long longValue() {
		return (long)doubleValue();
	}
	
	/**
	 * This will left shift an integer represented by a boolean array.
	 * @param toShift The number to shift.
	 * @return The shifted number.
	 */
	protected boolean[] leftShift(final boolean[] toShift){
		boolean[] toRet = new boolean[toShift.length];
		toRet[0] = false;
		System.arraycopy(toShift, 0, toRet, 1, toShift.length-1);
		return toRet;
	}
	
	/**
	 * This will sign extend an integer represented by a boolean array.
	 * @param toExt The integer to extend.
	 * @param numDigits The number of digits to extend it to (can be less than the array length for truncation).
	 * @return The new integer.
	 */
	protected boolean[] signExtend(final boolean[] toExt, final int numDigits){
		final boolean[] toRet = Arrays.copyOf(toExt, numDigits);
		if(numDigits > toExt.length){
			Arrays.fill(toRet, toExt.length, numDigits, toExt[toExt.length-1]);
		}
		return toRet;
	}
	
	/**
	 * This adds two integers stored as boolean arrays.
	 * @param int1 The first integer to add.
	 * @param int2 The second integer to add.
	 * @return The result of the addition.
	 */
	protected boolean[] add(final boolean[] int1, final boolean[] int2){
		boolean[] toRet = new boolean[int1.length];
		boolean carry = false;
		for(int i = 0; i<int1.length; i++){
			toRet[i] = int1[i] ^ int2[i] ^ carry;
			carry = (int1[i] && int2[i]) || (carry && (int1[i] ^ int2[i]));
		}
		return toRet;
	}
}