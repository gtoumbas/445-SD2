// Redistricting.java: Testing code for redistricting assignment
// COS 445 SD2, Spring 2020

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Redistricting extends Tournament<Party, RedistrictingConfig> {
  Redistricting(List<String> partyNames) {
    super(Party.class, partyNames);
  }

  private static class Pair {
    public int l;
    public int r;

    public Pair(int l, int r) {
      this.l = l;
      this.r = r;
    }
  }

  private static class ScorePair {
    public Pair l;
    public Pair r;

    public ScorePair(Pair lscore, Pair rscore) {
      this.l = lscore;
      this.r = rscore;
    }
  }

  private static void err(String netid, String err) {
    System.err.println(netid + err);
  }

  private static void checkLegalCut(String netid, List<List<Block>> cut, int numDistrictsRemaining,
      Set<Block> remaining) {
    assert cut != null : netid + ": returned null cut";
    assert cut.size() == numDistrictsRemaining : netid + ": wrong number of districts";
    int numBlocksPerDistrict = remaining.size() / numDistrictsRemaining;
    Set<Block> seenSoFar = new HashSet<Block>();
    for (List<Block> district : cut) {
      assert district != null : netid + ": returned null district in cut";
      assert district.size() == numBlocksPerDistrict : netid + ": outputs districts with wrong number of blocks";
      assert remaining.containsAll(district) : netid + ": includes other blocks";
      seenSoFar.addAll(district);
    }
    assert seenSoFar.containsAll(remaining) : netid + ": does not include all the remaining blocks";
  }

  private static void fixLegalCut(String netid, List<List<Block>> cut, int numDistrictsRemaining,
      Set<Block> remaining) {
    assert cut != null : netid + ": returned null cut";
    assert cut.size() == numDistrictsRemaining : netid + ": wrong number of districts";
    int numBlocksPerDistrict = remaining.size() / numDistrictsRemaining;
    Set<Block> seenSoFar = new HashSet<Block>();
    Set<List<Block>> seenDistricts = new HashSet<List<Block>>();
    for (List<Block> district : cut) {
      assert district != null : netid + ": returned null district in cut";
      assert seenDistricts.add(district) : netid + ": returned duplication of a district";
      if (district.size() > numBlocksPerDistrict) {
        err(netid, ": outputs districts with too many blocks");
        district.subList(numBlocksPerDistrict, district.size()).clear();
      }
      assert remaining.containsAll(district) : netid + ": includes other blocks";
      for (Iterator<Block> it = district.iterator(); it.hasNext();) {
        if (!seenSoFar.add(it.next())) {
          err(netid, ": repeated block");
          it.remove();
        }
      }
    }
    if (!seenSoFar.containsAll(remaining)) {
      err(netid, ": does not include all the remaining blocks");
      Set<Block> missing = new HashSet<Block>(remaining);
      missing.removeAll(seenSoFar);
      assert missing.size() > 0;
      Iterator<Block> missingIt = missing.iterator();
      // System.err.println("districts: " + cut.size() + " missing: " +
      // missing.size());
      for (List<Block> district : cut) {
        // System.err.print("Before " + district.size() + ": ");
        // for (Block b : district) {
        // System.err.print(" " + b);
        // }
        // System.err.println();
        while (district.size() < numBlocksPerDistrict) {
          district.add(missingIt.next());
          missingIt.remove();
        }
        // System.err.print("After " + district.size() + ": ");
        // for (Block b : district) {
        // System.err.print(" " + b);
        // }
        // System.err.println();
      }
      if (missing.size() != 0) {
        // System.err.print("missing:");
        // for (Block b : missing) {
        // System.err.print(" " + b);
        // }
        // System.err.println();
        // System.err.print("remaining:");
        // for (Block b : remaining) {
        // System.err.print(" " + b);
        // }
        // System.err.println();
        assert false : missing.size() + " " + numBlocksPerDistrict + " " + remaining.size();
      }
    }
    checkLegalCut(netid, cut, numDistrictsRemaining, remaining);
  }

  public static void checkLegalChoice(String netid, List<Block> choice, List<List<Block>> districts) {
    assert choice != null : netid + ": returned null choice";
    for (List<Block> district : districts)
      if (choice == district)
        return;
    assert false : netid + ": illegal choice";
  }

  public static List<Block> fixLegalChoice(String netid, List<Block> choice, List<List<Block>> districts) {
    if (choice == null) {
      System.err.println(netid + ": returned null choice");
      return districts.get(rand.nextInt(districts.size()));
    }
    for (List<Block> district : districts)
      if (choice == district)
        return choice;
    System.err.println(netid + ": illegal choice");
    return districts.get(rand.nextInt(districts.size()));
  }

  public double[] runTrial(List<Class<? extends Party>> strategies, RedistrictingConfig config) {
    double[] score = new double[strategies.size()];
    final int numDistricts = config.getd();
    final int numBlocks = config.getN(numDistricts);
    final int T = config.getT(numDistricts, numBlocks);

    // Uncomment this to suppress output.
    // PrintStream stdout = System.out;
    // System.setOut(new PrintStream(OutputStream.nullOutputStream()));

    // Randomize order of trials
    List<Pair> trials = new ArrayList<Pair>();
    for (int l = 0; l < strategies.size(); ++l) {
      for (int r = 0; r < l; ++r) {
        trials.add(new Pair(l, r));
        trials.add(new Pair(r, l));
      }
    }
    Collections.shuffle(trials);

    trials.parallelStream().map((trial) -> {

      final int c = 1;
      final int alpha_star = c * T + rand.nextInt(T + 1);
      final int beta_star = c * T + rand.nextInt(T + 1);
      ArrayList<Block> blocks = new ArrayList<Block>();
      for (int i = 0; i < numBlocks; ++i) {
        blocks.add(new Block(rand.nextInt(alpha_star + 1) + 1, rand.nextInt(beta_star + 1) + 1));
      }
      boolean alpha_crashed = false;
      boolean beta_crashed = false;

      Party alpha = null;
      Party beta = null;

      try {
        alpha = runWithTimeout(strategies.get(trial.l).getSimpleName() + ": took too long to construct", () -> {
          return (Party) strategies.get(trial.l).getMethod("New", boolean.class, int.class, List.class).invoke(null,
              false, numDistricts, Collections.unmodifiableList(blocks));
        }, 200);
        assert alpha != null : strategies.get(trial.l).getSimpleName() + ": constructed null instance";
      } catch (Exception e) {
        System.err.println(e + " alpha crashed.");
        alpha_crashed = true;
      }

      if (!alpha_crashed) {
        try {
          beta = runWithTimeout(strategies.get(trial.r).getSimpleName() + ": took too long to construct", () -> {
            return (Party) strategies.get(trial.r).getMethod("New", boolean.class, int.class, List.class).invoke(null,
                true, numDistricts, Collections.unmodifiableList(blocks));
          }, 200);
          assert beta != null : strategies.get(trial.r).getSimpleName() + ": constructed null instance";
        } catch (Exception e) {
          System.err.println(e + " beta crashed.");
          beta_crashed = true;
        }
      }

      ArrayList<List<Block>> districts = new ArrayList<List<Block>>();
      Party active = alpha;
      Party nonactive = beta;

      Set<Block> remaining = new HashSet<Block>(blocks);
      for (int r = numDistricts; r > 0 && (!alpha_crashed && !beta_crashed); --r) {
        final Party _active = active, _nonactive = nonactive;
        final int _r = r;
        final List<Block> _remaining = Collections.unmodifiableList(new ArrayList<Block>(remaining));

        List<List<Block>> cut;
        List<List<Block>> _cut;
        try {
          cut = runWithTimeout(active.getClass().getSimpleName() + ": took too long to cut", () -> {
            return _active.cut(_r, _remaining);
          }, 200);
          fixLegalCut(active.getClass().getSimpleName(), cut, r, remaining);
          cut = cut.stream().map(l -> Collections.unmodifiableList(l)).collect(Collectors.toList());
          _cut = Collections.unmodifiableList(cut);
        } catch (Exception e) {
          if (active == alpha) {
            System.err.println(e + " alpha crashed.");
            alpha_crashed = true;
          } else {
            System.err.println(e + " beta crashed.");
            beta_crashed = true;
          }
          break;
        }

        List<Block> choice;
        try {
          choice = runWithTimeout(nonactive.getClass().getSimpleName() + ": took too long to choose", () -> {
            return _nonactive.choose(_cut);
          }, 200);
          choice = fixLegalChoice(nonactive.getClass().getSimpleName(), choice, cut);
        } catch (Exception e) {
          if (nonactive == alpha) {
            System.err.println(e + " alpha crashed.");
            alpha_crashed = true;
          } else {
            System.err.println(e + " beta crashed.");
            beta_crashed = true;
          }
          break;
        }

        try {
          final List<Block> _choice = choice;
          runWithTimeout(active.getClass().getSimpleName() + ": took too long to accept", () -> {
            _active.accept(Collections.unmodifiableList(_choice));
            return null;
          }, 200);
        } catch (Exception e) {
          if (active == alpha) {
            System.err.println(e + " alpha crashed.");
            alpha_crashed = true;
          } else {
            System.err.println(e + " beta crashed.");
            beta_crashed = true;
          }
          break;
        }

        remaining.removeAll(choice);
        districts.add(choice);
        active = _nonactive;
        nonactive = _active;
      }
      int score_l, score_r;

      score_l = 0;
      score_r = 0;
      if (alpha_crashed) {
        score_r = districts.size();
      } else if (beta_crashed) {
        score_l = districts.size();
      } else {
        for (List<Block> district : districts) {
          int alpha_votes = 0;
          int beta_votes = 0;
          for (Block b : district) {
            alpha_votes += b.alpha();
            beta_votes += b.beta();
          }
          if (alpha_votes > beta_votes) {
            score_l++;
          } else {
            score_r++;
          }
        }
      }

      return new ScorePair(new Pair(trial.l, score_l), new Pair(trial.r, score_r));
    }).sequential().forEach((sp) -> {
      score[sp.l.l] += sp.l.r;
      score[sp.r.l] += sp.r.r;
    });

    for (int i = 0; i < score.length; ++i) {
      score[i] /= 2 * (strategies.size() - 1);
    }

    // Uncomment this if you are suppressing output.
    // System.setOut(stdout);
    return score;
  }

  public static void main(String[] args) throws java.io.FileNotFoundException {
    assert args.length >= 1 : "Expected filename of strategies as first argument";
    final int numTrials = 25;
    // New Jersey has 40 state senate seats, 565 municipalities, and had 3169310
    // votes cast in 2018
    final RedistrictingConfig config = new RedistrictingConfig(40, 560, 3169310);
    final BufferedReader namesFile = new BufferedReader(new FileReader(args[0]));
    final List<String> strategyNames = namesFile.lines().map(s -> String.format("Party_%s", s))
        .collect(Collectors.toList());
    // each strategy in the sample room with the sample strategies (not a component
    // of the grade,
    // just for overfitting comparisons)
    final Redistricting withStrategies = new Redistricting(strategyNames);

    try {
      double[] res = withStrategies.oneEachTrials(numTrials, config);
      System.out.println("netID,score");
      for (int i = 0; i < strategyNames.size(); ++i) {
        System.out.println(strategyNames.get(i).substring(6) + "," + Double.toString(res[i]));
      }
    } catch (Exception e) {
      service.shutdownNow();
      try {
        if (!service.awaitTermination(100, TimeUnit.MICROSECONDS)) {
          System.out.println("Still waiting...");
        }
      } catch (Exception f) {
        throw new RuntimeException(f);
      }
      System.out.println("Exiting normally...");
      throw e;
    }
    System.exit(0);
  }
}
