// Party_even_cut_pack_choose.java: sample implementation for Party
// COS 445 SD2, Spring 2019
// Created by Andrew Wonnacott

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party_vn7241 implements Party {
  final boolean _isBeta;

  private Party_vn7241(boolean isBeta) {
    _isBeta = isBeta;
  }
  // must construct and return a Party based on the provided blocks. Do any initialization here.
  public static Party New(boolean isBeta, int numDistricts, List<Block> blocks) {
    return new Party_vn7241(isBeta);
  }

  // Make the most even districts easily possible (e.g. greedily put the districts with the biggest
  // difference together)
  public List<List<Block>> cut(int r, List<Block> remaining) {
    remaining = new ArrayList<Block>(remaining); // get mutability
    Collections.sort(remaining, new Block.BlockComparator(false, true, false));
    // now increasing order of alpha - beta
    int tot_size = remaining.size();
    final int districtSize = remaining.size() / r;
    List<List<Block>> ret = new ArrayList<List<Block>>();
    long[] betaSwings = new long[r];
    for (int i = 0; i < r; ++i) {
      List<Block> district = new ArrayList<Block>();
      ret.add(district);
    }

    //calculate cutoff at which votes are balanced
    int cutoff = 0;
    int reverseCutoff = remaining.size() - 1;
    long totalSwing = 0;
    long totalSwingRev = 0;
    List<Block> remaining_copy = new ArrayList<Block>(remaining);
    List<Block> remaining_rev = new ArrayList<Block>(remaining);
    Collections.sort(remaining_rev, new Block.BlockComparator(false, true, true));

    int idx1 = 0;
    int dist_idx = 0;
    int wait = 0;
    for (Block block : remaining) {
      if((totalSwing>0)){
        cutoff = idx1;
        //break;
        if(_isBeta==true){
          ret.get(dist_idx).add(remaining.get(idx1));
          betaSwings[dist_idx] += remaining.get(idx1).betaSwing();
          remaining_copy.remove(remaining_copy.size()-1);
          if(ret.get(dist_idx).size() == districtSize) dist_idx += 1;
        }
        
      }
      totalSwing += -block.betaSwing();
      idx1++;
    }

    if(cutoff == 0){
      int idx2 = remaining.size()-1;
      dist_idx = 0;
      for (Block block : remaining_rev) {
        if((totalSwingRev<0)){
          reverseCutoff = idx2;
          //break;
          if(_isBeta==false){
            ret.get(dist_idx).add(remaining.get(idx2));
            betaSwings[dist_idx] += remaining.get(idx2).betaSwing();
            remaining_copy.remove(0);
            if(ret.get(dist_idx).size() == districtSize) dist_idx += 1;
          }
        }
        totalSwingRev += -block.betaSwing();
        idx2--;
      }
      if(_isBeta==false){
        while(ret.get(dist_idx).size() < districtSize){
          ret.get(dist_idx).add(remaining_copy.get(0));
          betaSwings[dist_idx] += remaining_copy.get(0).betaSwing();
          remaining_copy.remove(0);
        }
      }
    }else{
      if(_isBeta==true){
        while(ret.get(dist_idx).size() < districtSize){
          ret.get(dist_idx).add(remaining_copy.get(remaining_copy.size()-1));
          betaSwings[dist_idx] += remaining_copy.get(remaining_copy.size()-1).betaSwing();
          remaining_copy.remove(remaining_copy.size()-1);
        }
      }
    }
    

    // sort remaining blocks normally and do the same as even cut greedy
    Collections.sort(remaining_copy, new Block.BlockComparator(true, true, true));
    //Collections.shuffle(remaining_copy);
    // this is inefficient, but idrc
    for (Block block : remaining_copy) {
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
