// Block.java: helper class for a block of voters for alpha and beta
// COS 445 SD2, Spring 2020
// Created by Andrew Wonnacott, Frankie Lam

import java.util.Comparator;
import java.util.List;

public final class Block {
  final long _a;
  final long _b;

  public Block(long a, long b) {
    _a = a;
    _b = b;
  }

  public long alpha() {
    return _a;
  }

  public long beta() {
    return _b;
  }

  public boolean equals(Block x){
    return (x.alpha() == _a) && (x.beta() == _b);
  }

  public long betaSwing() {
    return _b - _a;
  }

  public long numVoters() {
    return _b + _a;
  }

  public static class BlockComparator implements Comparator<Block> {
    final boolean _byBeta;
    final boolean _byDifference;
    final boolean _reverse;
    // By default, compares by amount of alpha (ascending)
    // byBeta = compare by amount of beta
    // byDifference = compare by how much alpha beats beta
    // byBeta && byDifference = compare by magnitudes instead (closest blocks first)
    // so byDifference && reverse = compare by how much beta beats alpha
    // so byDifference && byBeta && reverse = compare by magnitudes instead (most partisan blocks
    // first)
    public BlockComparator(boolean byBeta, boolean byDifference, boolean reverse) {
      _byBeta = byBeta;
      _byDifference = byDifference;
      _reverse = reverse;
    }

    @Override
    public int compare(Block lhs, Block rhs) {
      int ret = 0;
      if (_byDifference) {
        if (_byBeta) {
          ret = Long.compare(Math.abs(lhs._a - lhs._b), Math.abs(rhs._a - rhs._b));
        } else {
          ret = Long.compare(lhs._a - lhs._b, rhs._a - rhs._b);
        }
      } else if (_byBeta) {
        ret = Long.compare(lhs._b, rhs._b);
      } else {
        ret = Long.compare(lhs._a, rhs._a);
      }

      // Tie break by total voters to produce a complete ordering
      if (ret == 0) ret = Long.compare(lhs._a + lhs._b, rhs._a + rhs._b);
      return _reverse ? -ret : ret;
    }
  }

  public Block aggregate(List<Block> district){
    long a = 0;
    long b = 0;
    for (Block block : district){
      a = a + block._a;
      b = b + block._b;
    }
    return new Block(a,b);
  }

}
