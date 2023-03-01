// Party_pack_cut_pack_choose.java: sample implementation for Party
// COS 445 SD2, Spring 2019
// Created by Andrew Wonnacott

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party_pack_cut_pack_choose implements Party {
  final boolean _isBeta;

  private Party_pack_cut_pack_choose(boolean isBeta) {
    _isBeta = isBeta;
  }
  // must construct and return a Party based on the provided blocks. Do any initialization here.
  public static Party New(boolean isBeta, int numDistricts, List<Block> blocks) {
    return new Party_pack_cut_pack_choose(isBeta);
  }

  // Make the most uneven districts possible (e.g. make the district with the most margin for alpha
  // voters, then district with the most margin for beta voters, repeat)
  public List<List<Block>> cut(int numDistrictsRemaining, List<Block> remaining) {
    remaining = new ArrayList<Block>(remaining); // get mutability
    Collections.sort(remaining, new Block.BlockComparator(false, true, !_isBeta));
    // now sorted in increasing order of how many more votes the other party gets
    List<List<Block>> ret = new ArrayList<List<Block>>();
    final int districtSize = remaining.size() / numDistrictsRemaining;
    for (int i = 0; i < numDistrictsRemaining; ++i) {
      List<Block> district = new ArrayList<Block>();
      for (int j = 0; j < districtSize; ++j) {
        district.add(remaining.get(i * districtSize + j));
      }
      ret.add(district);
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
