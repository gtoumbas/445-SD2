// Party_even_cut_pack_choose.java: sample implementation for Party
// COS 445 SD2, Spring 2019
// Created by Andrew Wonnacott

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party_even_cut_pack_choose implements Party {
  final boolean _isBeta;

  private Party_even_cut_pack_choose(boolean isBeta) {
    _isBeta = isBeta;
  }
  // must construct and return a Party based on the provided blocks. Do any initialization here.
  public static Party New(boolean isBeta, int numDistricts, List<Block> blocks) {
    return new Party_even_cut_pack_choose(isBeta);
  }

  // Make the most even districts easily possible (e.g. greedily put the districts with the biggest
  // difference together)
  public List<List<Block>> cut(int r, List<Block> remaining) {
    remaining = new ArrayList<Block>(remaining); // get mutability
    Collections.sort(remaining, new Block.BlockComparator(true, true, true));
    // now sorted in decreasing order of partisanship
    final int districtSize = remaining.size() / r;
    List<List<Block>> ret = new ArrayList<List<Block>>();
    long[] betaSwings = new long[r];
    for (int i = 0; i < r; ++i) {
      List<Block> district = new ArrayList<Block>();
      ret.add(district);
    }
    // this is inefficient, but idrc
    for (Block block : remaining) {
      int extremum = -1;
      for (int i = 0; i < r; ++i) {
        List<Block> district = ret.get(i);
        if (district.size() == districtSize) continue;
        if (extremum == -1) extremum = i;
        // we swing beta and this is the most alpha blockyet
        if (block.betaSwing() > 0 && betaSwings[i] < betaSwings[extremum]) extremum = i;
        // we swing alpha and this is the most beta block yet
        if (block.betaSwing() < 0 && betaSwings[i] > betaSwings[extremum]) extremum = i;
        // if we hit zero swing blocks then it doesn't matter anyhow;
      }
      ret.get(extremum).add(block);
      // pulls down the swing on the beta swing
      betaSwings[extremum] += block.betaSwing();
    }
    return ret;
  }

  // Return the district with the most voters for the opponent
  public List<Block> choose(List<List<Block>> districts) {
    long mostOppVoters = -1;
    List<Block> mostOppVotersDistrict = null;
    for (List<Block> district : districts) {
      long oppVoters = 0;
      for (Block block : district) {
        oppVoters += _isBeta ? block.alpha() : block.beta();
      }
      if (oppVoters > mostOppVoters) {
        mostOppVoters = oppVoters;
        mostOppVotersDistrict = district;
      }
    }
    return mostOppVotersDistrict;
  }

  // inform the active party of the choice made by the nonactive party
  public void accept(List<Block> chosen) {}
}
