// Party.java: interface for Party
// COS 445 SD2, Spring 2020
// Created by Andrew Wonnacott

import java.lang.invoke.MethodHandles;
import java.util.List;

public interface Party {
  /* We guarantee that we will always call the methods in this order:
   * * New on the class of each of alpha and beta
   * * Repeating \texttt{numDistricts} times, with the active player initially alpha:
   *   * cut on the active party
   *   * choose on the nonactive party
   *   * accept on the active party
   *   * swap the active and nonactive parties
   */

  /* Each of the following functions must terminate within the time limit of 200 ms */

  // must construct and return a Party based on the provided blocks. Do any initialization here.
  public static Party New(boolean isBeta, int numDistricts, List<Block> blocks) {
    throw new RuntimeException(
        "Submission did not override Party.New: " + MethodHandles.lookup().lookupClass());
  }

  // must partition the remaining blocks into numDistricts, each with remaining.size() /
  // numDistricts elements
  // We will issue a penalty if your strategy does any of the following:
  // * outputs the wrong number of districts
  // * outputs districts with different numbers of blocks
  // * does not include all the remaining blocks
  // * includes any other blocks.
  public List<List<Block>> cut(int numDistrictsRemaining, List<Block> remaining);

  // must chose and return one of the provided districts
  // We will issue a penalty if your strategy does not return one of the provided lists
  public List<Block> choose(List<List<Block>> districts);

  // inform the active party of the choice made by the nonactive party
  public void accept(List<Block> chosen);
}
