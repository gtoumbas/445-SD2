// Party_even_cut_pack_choose.java: sample implementation for Party
// COS 445 SD2, Spring 2019
// Created by Andrew Wonnacott

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party_qaz123 implements Party {
  final boolean _isBeta;

  private Party_qaz123(boolean isBeta) {
    _isBeta = isBeta;
  }
  // must construct and return a Party based on the provided blocks. Do any initialization here.
  public static Party New(boolean isBeta, int numDistricts, List<Block> blocks) {
    return new Party_qaz123(isBeta);
  }

  // Make the most even districts easily possible (e.g. greedily put the districts with the biggest
  // difference together)
  public List<List<Block>> cut(int r, List<Block> remaining) {
    remaining = new ArrayList<Block>(remaining); // get mutability
    Collections.sort(remaining, new Block.BlockComparator(false, true, false));
    // now increasing order of alpha - beta
    final int districtSize = remaining.size() / r;
    List<List<Block>> ret = new ArrayList<List<Block>>();
    long[] betaSwings = new long[r];
    for (int i = 0; i < r; ++i) {
      List<Block> district = new ArrayList<Block>();
      ret.add(district);
    }

    //calculate cutoff at which votes are balanced
    int cutoff = 0;
    long minTot = Math.abs(remaining.get(cutoff).alpha()-remaining.get(cutoff).beta());
    int reverseCutoff = remaining.size() - 1;
    long revMinTot = Math.abs(remaining.get(reverseCutoff).alpha()-remaining.get(reverseCutoff).beta());
    long totalSwing = minTot;
    long totalSwingRev = revMinTot;
    List<Block> remaining_copy = new ArrayList<Block>();
    remaining_copy = remaining;

    for (int i = 0; i < remaining.size(); ++i) {
      if(minTot > Math.abs(totalSwing)){
        minTot = Math.abs(totalSwing);
        cutoff = i;
      }
      if(revMinTot > Math.abs(totalSwingRev)){
        revMinTot = Math.abs(totalSwingRev);
        reverseCutoff = remaining.size() - i;
      }
      totalSwing += remaining.get(i).alpha()-remaining.get(i).beta();
      totalSwingRev += remaining.get(remaining.size() - i).alpha()-remaining.get(remaining.size() - i).beta();
    }

    // remove blocks before or after cutoff
    if (cutoff > 0 || reverseCutoff < remaining.size()-1){
      if (totalSwing>0 && _isBeta==true){
        int dist_idx = 0;
        for (int i = 0; i < remaining.size(); ++i) {
          if(remaining.size()-i > cutoff){
            ret.get(dist_idx).add(remaining.get(remaining.size()-i));
            betaSwings[dist_idx] += remaining.get(remaining.size()-i).betaSwing();
            remaining_copy.remove(remaining_copy.size()-1);
            if(ret.get(dist_idx).size() == districtSize) dist_idx += 1;
          }
        }

      }
      if (totalSwing<0 && _isBeta==false){
        cutoff = reverseCutoff;
        int dist_idx = 0;
        for (int i = 0; i < remaining.size(); ++i) {
          if(i < cutoff){
            ret.get(dist_idx).add(remaining.get(i));
            betaSwings[dist_idx] += remaining.get(i).betaSwing();
            remaining_copy.remove(0);
            if(ret.get(dist_idx).size() == districtSize) dist_idx += 1;
          }
        }
      }
    }

    // sort remaining blocks normally and do the same as even cut greedy
    Collections.sort(remaining_copy, new Block.BlockComparator(true, true, true));
    remaining = remaining_copy;

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

    // Return the district we win which we win by the closest margin (or, if none, the one we lose by
  // the largest)
  public List<Block> choose(List<List<Block>> districts) {
    long closestWinMargin = -1;
    List<Block> closestWinDistrict = null;
    long furthestLossMargin = -1;
    List<Block> furthestLossDistrict = null;
    for (List<Block> district : districts) {
      long ourFavor = 0;
      for (Block block : district) {
        ourFavor += _isBeta ? block.betaSwing() : -block.betaSwing();
      }
      // n.b. tiebreaks in favor of beta
      if (ourFavor > 0 || (ourFavor == 0 && _isBeta)) {
        if (closestWinDistrict == null || closestWinMargin > ourFavor) {
          closestWinMargin = ourFavor;
          closestWinDistrict = district;
        }
      } else {
        // ourFavor is negative, so store the min
        if (furthestLossDistrict == null || furthestLossMargin > ourFavor) {
          furthestLossMargin = ourFavor;
          furthestLossDistrict = district;
        }
      }
    }
    return (closestWinDistrict != null) ? closestWinDistrict : furthestLossDistrict;
  }
  // inform the active party of the choice made by the nonactive party
  public void accept(List<Block> chosen) {}
}