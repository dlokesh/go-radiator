(ns go-radiator.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [go-radiator.core :refer :all]))

(facts "about pipeline status"

       (fact "it should return building if any of the stages are building"
             (derive-status [{:attrs {:activity "Sleeping"}},
                             {:attrs {:activity "Building"}}])
             => "building")

       (fact "it should return failed if any of the stages has failed"
             (derive-status [{:attrs {:activity "Sleeping", :lastBuildStatus "Passed"}},
                             {:attrs {:activity "Sleeping", :lastBuildStatus "Failure"}}])
             => "failed")

       (fact "it should return passed if all of the stages have passed"
             (derive-status [{:attrs {:lastBuildStatus "Passed"}},
                             {:attrs {:lastBuildStatus "Passed"}}])
             => "passed"))

(facts "about pipeline name"

       (fact "it should extract pipeline name"
             (pipeline-name {:attrs {:name "PipelineOne :: StageOne"}})
             => "PipelineOne "))

(facts "about pipeline grouping"

       (fact "it should group pipelines by name"
             (pipelines-by-name {:content ["p1" "p2"]}) => {"p1" ["p1"], "p2" ["p2"]}
             (provided
               (pipeline-name "p1") => "p1"
               (pipeline-name "p2") => "p2")))
