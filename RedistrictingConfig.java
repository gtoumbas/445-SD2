// RedistrictingConfig.java: Code for redistricting assignment
// COS 445 SD2, Spring 2020
// Created by Andrew Wonnacott

public class RedistrictingConfig {
  protected int _d;
  protected int _N_per_d;
  protected int _T;

  public RedistrictingConfig(int d, int N, int T) {
    _d = d; // number of districts
    _N_per_d = N / d; // blocks per district
    _T = T; // expected voters per district
  }

  public int getd() {
    return _d;
  }
  // must return a multiple of d
  public int getN(int d) {
    return _N_per_d * d;
  }

  public int getT(int d, int N) {
    return _T;
  }
}
