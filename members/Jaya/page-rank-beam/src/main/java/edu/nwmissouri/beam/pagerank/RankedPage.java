
package edu.nwmissouri.beam.pagerank;

import java.io.Serializable;
import java.util.ArrayList;

// beam-playground:
//   name: MinimalWordCount
//   description: An example that counts words in Shakespeare's works.
//   multifile: false
//   pipeline_options:
//   categories:
//     - Combiners
//     - Filtering
//     - IO
//     - Core Transforms

public class RankedPage implements Serializable {
        String name;
        double rank;
        int count;
        private ArrayList<VotingPage> Voted;

        public RankedPage(String name, double rank, int count) {
                this.name = name;
                this.rank = rank;
                this.count = count;
        }

        public RankedPage(String name, Double rank, ArrayList<VotingPage> Voted) {
                this.name = name;
                this.rank = rank;
                this.Voted = Voted;
        }

        public RankedPage(String name, ArrayList<VotingPage> Voted) {
                this.name = name;
                this.Voted = Voted;
        }

        public RankedPage(String name, int rank) {
                this.name = name;
                this.rank = rank;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public double getRank() {
                return rank;
        }

        public void setRank(double rank) {
                this.rank = rank;
        }

        public int getCount() {
                return count;
        }

        public void setCount(int count) {
                this.count = count;
        }

        public ArrayList<VotingPage> getVoted() {
                return Voted;
        }

        public void setVoted(ArrayList<VotingPage> voted) {
                this.Voted = voted;
        }

        @Override
        public String toString() {
                return "RankedPage [name=" + name + ", rank=" + rank + ", count=" + count + "]";
        }

}
