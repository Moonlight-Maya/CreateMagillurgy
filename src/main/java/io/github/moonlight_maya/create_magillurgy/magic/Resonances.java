package io.github.moonlight_maya.create_magillurgy.magic;

public class Resonances {
	//Resonances are stored as ints. This class offers static helpers to modify them.
	//Each 4 bits is one number (as a consequence, each resonance can have elements only 0-15)
	//The order is the same as in the Elements enum, low bits to high bits
	//Life, Ender, Soul, Void, Astral, Nether, Arcane
	//29th bit is bitmask for if it's stabilized. If stabilized, opposite elements won't collapse.
	private final static int STABILITY_BIT = 1<<28;

	//Getters and setters for ints
	public static int getElem(int resonance, MagicElement element) {
		int index = element.ordinal() * 4;
		return (resonance & (0xF << index)) >> index;
	}
	public static int setElem(int resonance, MagicElement element, int value) {
		int index = element.ordinal() * 4;
		return (resonance & ~(0xF << index)) | (value << index);
	}
	public static int changeElem(int resonance, MagicElement element, int diff) {
		return setElem(resonance, element, getElem(resonance, element) + diff);
	}
	public static boolean isStable(int resonance) {
		return (resonance & STABILITY_BIT) > 0;
	}
	public static int makeStable(int resonance) {
		return resonance | STABILITY_BIT;
	}
	public static int makeUnstable(int resonance) {
		return resonance & STABILITY_BIT;
	}

	//Letters are the first letter of each type, except for Arcane which is R.
	//If an asterisk is included, the resonance is made stable.
	//This is fine for reading from recipes/configs, and is human-readable,
	//but this should be called infrequently.
	//The values should be stored and operated upon as ints
	//Example: the string "RR*SA" would give a stable resonance with 2 arcane, 1 soul, and 1 astral.
	//The characters could be arranged in any order and give the same result.
	public static int fromString(String str) {
		int resonance = 0;
		for (char c : str.toCharArray()) {
			switch (c) {
				case 'L', 'l' -> resonance = changeElem(resonance, MagicElement.LIFE, 1);
				case 'E', 'e' -> resonance = changeElem(resonance, MagicElement.ENDER, 1);
				case 'S', 's' -> resonance = changeElem(resonance, MagicElement.SOUL, 1);
				case 'V', 'v' -> resonance = changeElem(resonance, MagicElement.VOID, 1);
				case 'A', 'a' -> resonance = changeElem(resonance, MagicElement.ASTRAL, 1);
				case 'N', 'n' -> resonance = changeElem(resonance, MagicElement.NETHER, 1);
				case 'R', 'r' -> resonance = changeElem(resonance, MagicElement.ARCANE, 1);
				case '*' -> resonance = makeStable(resonance);
				default -> throw new IllegalArgumentException("Unexpected character in resonance string: " + c);
			}
		}
		return resonance;
	}
}
