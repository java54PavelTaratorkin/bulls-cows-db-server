package telran.net.games.service;

import java.util.Random;
import java.util.stream.Collectors;

import telran.net.games.exceptions.*;
import telran.net.games.model.*;

public class BullsCowsGameRunner {
	private static final int MIN_SEQ_LENGTH = 2;
	private static final int MAX_SEQ_LENGTH = 10;
	private static final int MIN_AGE = 13;
	private static final int DEF_SEQ_LENGTH = 4;
	int minSequenceLength;
	int maxSequenceLength;
	int defSequenceLength;
	int minAge;

	public BullsCowsGameRunner() {
		this.minSequenceLength = MIN_SEQ_LENGTH;
		this.maxSequenceLength = MAX_SEQ_LENGTH;
		this.defSequenceLength = DEF_SEQ_LENGTH;
		this.minAge = MIN_AGE;
	}
	
	public BullsCowsGameRunner(int minSequenceLength, int maxSequenceLength, 
			int defSequenceLength, int minAge) {		
		this.minSequenceLength = minSequenceLength;
		this.maxSequenceLength = maxSequenceLength;
		this.defSequenceLength = defSequenceLength;
		this.minAge = minAge;
	}

	public String getRandomSequence(int sequenceLength) {
		sequenceLength = getSequenceLength(sequenceLength);
		String toBeGuessed = new Random()
				.ints(0, 10)
				.distinct()
				.limit(sequenceLength)
				.mapToObj(Integer::toString)
				.collect(Collectors.joining());
		return toBeGuessed;
	}

	private int getSequenceLength(int sequenceLength) {
		if (sequenceLength != 0 && 
				(sequenceLength < MIN_SEQ_LENGTH || sequenceLength > MAX_SEQ_LENGTH)) {
			throw new IncorrectSequenceLengthException(MIN_SEQ_LENGTH, MAX_SEQ_LENGTH);
		}
		sequenceLength = sequenceLength == 0 ? defSequenceLength : sequenceLength;
		return sequenceLength;
	}

	public MoveData moveProcessing(String guess, String toBeGuessed, int sequenceLength) {
		int[] bullsCows = { 0, 0 };
		int j = 0; // index in bullsCows; 0 - number of bulls, 1 - number of cows
		char chars[] = guess.toCharArray();
		for (int i = 0; i < sequenceLength; i++) {
			int index = toBeGuessed.indexOf(chars[i]);
			if (index >= 0) {
				j = index == i ? 0 : 1;
				bullsCows[j]++;
			}
		}
		return new MoveData(guess, bullsCows[0], bullsCows[1]);
	}
	
	public int getMinSequenceLength() {
		return minSequenceLength;
	}

	public int getMaxSequenceLength() {
		return maxSequenceLength;
	}

	public int getDefSequenceLength() {
		return defSequenceLength;
	}

	public int getMinAge() {
		return minAge;
	}

	boolean checkGuess(String guess, int sequenceLength) {
		return guess.matches("\\d+") && guess.chars().distinct().count() == sequenceLength;
	}

	boolean checkGameFinished(MoveData moveData, int sequenceLength) {
		return moveData.bulls() == sequenceLength;
	}

}
