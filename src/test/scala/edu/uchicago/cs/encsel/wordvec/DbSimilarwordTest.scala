/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contributors:
 *     Hao Jiang - initial API and implementation
 */

package edu.uchicago.cs.encsel.wordvec

import javax.persistence.Persistence

import org.junit.Assert._
import org.junit.{Before, Test}

class DbSimilarwordTest {

  @Before
  def prepare: Unit = {

    val em = Persistence.createEntityManagerFactory("word-vector").createEntityManager()

    em.getTransaction.begin()

    em.persist(new WordVector("avenue", "0.49404 0.1865 -0.31271 -0.18776 0.29679 -0.38144 -0.80533 -0.45511 0.14438 0.14507 -0.33376 -0.057126 0.014455 0.13315 -0.22241 -0.41444 0.46618 0.64422 0.49401 -0.17312 -0.72778 -0.48067 -0.42115 0.55346 0.13451 0.46233 0.10256 0.30642 0.35939 0.36144 -0.41686 -0.1983 0.38912 0.14092 -0.27787 -0.5014 0.44107 0.30843 -0.22981 -0.68161 -0.63591 0.34811 0.72147 -0.14795 0.15043 0.1489 -0.8236 0.17697 0.13868 0.34994 -0.068503 0.67862 0.33415 -0.17725 0.23719 0.37627 -0.51493 0.067337 -0.78022 -0.42098 -0.26025 -0.20366 -0.25177 -0.23894 0.26511 -0.28401 -0.56803 -0.44219 -0.33628 0.010853 -0.13594 -0.25651 -0.79933 -0.60401 -0.078891 -0.20346 -0.32559 -0.20145 -0.043007 -0.24749 0.098052 -0.012053 -0.52721 0.70429 0.16005 -0.22492 0.52472 -0.37379 0.33483 0.72503 0.16914 0.43677 0.073409 -0.29394 0.16879 -0.20786 -1.4093 0.079692 0.77748 -0.2197 0.25434 0.41109 -0.63449 0.67616 0.34959 -0.11903 -0.65174 -0.1778 -0.27725 0.67098 0.65391 -0.14138 -0.031963 0.016571 -0.025175 0.0024925 0.57206 0.44621 0.22394 -0.38492 -0.29036 0.20774 -0.10929 0.0038798 0.24576 0.27179 -0.42024 0.11359 0.28517 0.17293 1.0795 -0.48419 -0.62755 0.42138 0.94042 -0.19974 0.24824 0.34403 -0.3428 0.41463 -0.57292 0.059303 -0.37698 -0.3951 0.79412 -0.19733 -0.087466 -0.26606 -0.36599 0.20894 -0.26778 -0.76463 0.33281 -0.12165 0.08105 -0.44566 0.28435 0.34445 -0.11181 -0.038826 0.44441 -0.18427 0.23748 -0.64303 -0.14482 0.17024 0.49712 0.20207 -0.060494 -0.11592 -0.070083 0.47419 -0.62576 0.68594 -0.30389 0.27767 0.27156 0.27435 0.80822 0.39243 -0.2354 0.36198 -0.089303 0.2261 -0.11771 0.031416 0.23812 0.74833 -0.046742 0.088185 0.32529 -0.18609 -0.56276 0.09709 0.42689 -0.095012 -0.44014 0.09219 -0.57653 0.0014122 -0.43831 0.40152 0.052529 0.010234 0.085093 -0.19924 0.55851 0.32502 0.69939 0.18986 0.61626 -0.14015 -0.19314 0.08588 -0.215 0.6851 1.0442 0.1966 -0.16905 0.45218 -1.0018 0.21678 0.14839 -0.42589 -3.8194 -0.4066 -0.059205 0.17974 0.40503 0.17754 0.23534 0.14913 0.049764 -0.38493 0.052925 0.013748 0.18219 -0.27707 -0.19933 -0.54378 -0.38931 0.68669 -0.043203 0.29047 -0.63033 -0.23854 0.29556 -0.70416 -0.83497 0.25742 -0.20381 -0.14116 0.076475 0.08052 0.25545 -0.17378 0.35818 -0.026913 0.13362 -0.46171 0.22528 -0.54354 -0.39668 0.2735 0.074507 0.83773 -0.02607 0.025179 -0.088636 -0.10851 0.24479 0.30203 -0.64892 -0.47029 -0.85115 0.73659 -0.037503 -0.087907 0.17476 0.19809 -0.19189 -0.1798 0.079115 -0.24542 -0.129 0.8141 0.3406 -0.2325 0.043471 0.19598 -0.65811 -0.70417 -0.53277 0.40552 0.3472 -0.075152 0.47169 -0.82191 0.37864 -0.75424"))
    em.persist(new WordVector("chicago", "0.46441 0.17556 0.23809 0.45602 -0.15059 0.13759 -1.3554 -0.6271 -0.024367 0.01475 -0.010201 -0.28774 -0.076792 -0.31125 0.47816 -0.22781 0.079248 0.23297 0.18741 -0.22383 -0.61861 0.19305 -0.4599 0.24142 -0.39694 0.17871 -0.40394 0.17748 0.37744 -0.48943 -0.026469 0.60689 -0.1361 -0.012463 -0.15513 -0.27336 -0.22729 0.24301 -0.23009 0.45281 0.60101 0.27274 0.73229 -0.097865 0.15079 0.10704 0.58168 0.029993 -0.34754 0.43338 0.053771 0.38705 -0.023537 -0.23083 0.33769 0.40793 0.1842 -0.29798 -0.34518 0.32571 -0.44238 0.18726 -0.021894 -0.12413 0.11937 -0.33239 -0.36455 -0.1491 0.41013 -0.29135 0.010105 0.27892 0.29462 -0.30876 -0.65199 0.023873 -0.065043 -0.38034 0.67232 -0.18202 0.68676 -0.37514 -0.33257 -0.13587 0.36592 -0.14234 -0.16603 -0.12717 -0.17903 0.0064902 0.1119 0.24826 0.20662 0.035493 -0.067014 -0.02554 -2.1356 0.084944 0.36144 -0.16441 0.21296 -0.26081 -0.10458 -0.44568 0.13793 -0.025359 0.28367 0.042743 0.15688 0.14663 -0.15407 0.11664 -0.43685 0.0070799 -0.18188 0.62478 0.31177 0.096832 0.048117 0.23482 -0.2764 -0.38421 -0.2211 0.27965 -0.1147 0.64666 0.06854 -0.23105 0.087375 -0.61242 0.38381 -0.77439 0.082016 -0.042461 0.74764 0.026419 0.27101 0.52077 0.3596 0.5305 -0.028377 0.44302 -0.17175 -0.38954 -0.06011 0.22585 0.10154 0.25063 -0.59718 -0.034298 -0.30725 -0.30825 0.19943 0.36792 0.22865 -0.25858 0.44481 0.24063 -0.55466 -0.02919 0.053714 0.35481 -0.42923 0.29688 0.12065 0.22598 0.27443 -0.15789 0.11308 -0.32637 0.3979 -0.48298 0.13673 0.34167 0.072549 -0.38864 -0.11506 -0.22409 -0.06057 -0.11853 -0.31417 -0.25346 -0.097883 -0.18846 -0.38807 -0.34982 0.29011 0.16468 -0.038046 0.070089 0.41487 -0.062529 0.56335 -0.24273 -0.36571 -0.60352 -0.99862 0.17812 0.30394 0.6503 0.18476 0.30434 -0.1884 0.023957 -0.12558 0.0038385 -0.18349 0.32322 0.17122 0.34691 0.40847 -0.35098 -0.12004 -0.40178 0.015303 -0.53893 -0.21822 0.020231 0.082771 0.0092712 0.065352 0.23777 0.37846 0.34885 -3.5408 0.36354 -0.066381 0.37034 0.51532 -0.20764 -0.078374 0.24464 -0.51483 0.23848 0.2311 0.24328 -0.0032775 0.11885 -0.21495 -0.20859 -0.1138 0.34236 -0.24131 0.60351 0.26972 0.054094 0.015131 -0.48307 0.12872 -0.18952 -0.56354 0.6413 0.30545 0.097306 -0.19803 -0.20786 0.10561 0.073559 0.44097 -0.54384 0.32488 0.21128 -0.27894 -0.11474 0.35741 0.59446 0.16221 0.019925 0.16199 -0.080103 0.22039 0.16038 0.1624 0.58758 0.13204 0.20358 0.079208 -0.51715 -0.26823 0.01164 0.13237 0.50107 -0.49233 0.26579 0.26139 0.29203 -0.12109 -0.31538 0.14063 0.23246 -0.27846 -0.59498 0.012895 -0.025799 0.12705 -0.18381 0.39116 0.21832 0.056371 -0.70294"))
    em.persist(new WordVector("illinois", "-0.074431 -0.10409 -0.1796 0.043274 -0.75528 0.054634 -1.1952 -0.27119 0.091491 0.95427 -0.11998 0.2061 -0.18923 -0.40764 0.23315 0.2682 0.39026 0.31418 -0.046477 -0.11786 -0.46769 0.64445 -0.11925 0.23468 -0.092804 0.62595 -0.14745 0.13415 -0.2013 -0.38243 -0.83544 0.83259 0.074964 -0.12493 0.24547 0.28409 -0.58191 0.30647 0.026606 0.75912 0.46435 0.22758 0.67249 -0.25242 -0.012884 0.56633 0.73457 -0.15339 -0.094988 0.21108 0.039339 0.40713 0.00040531 0.11084 0.29037 0.9667 0.22205 -0.14577 -0.2651 0.32509 -0.31017 0.9655 -0.070495 -0.08697 -0.2277 -0.0024875 -0.0049888 0.17037 -0.042771 0.085395 0.085555 0.26655 0.42567 0.23259 0.06763 0.40776 0.024421 -0.50709 0.40284 -0.19311 0.1263 -0.4768 -0.47087 0.011881 -0.063679 -0.4112 0.06189 -0.18754 -0.18251 -0.38397 0.14692 -0.50082 0.31006 0.11077 -0.36824 -0.066222 -2.09 -0.29574 0.071169 0.19518 0.52703 -0.23704 -0.40853 -0.91151 0.1375 -0.21488 0.50415 -0.29798 0.32418 -0.017752 -0.10448 0.34164 0.19506 0.44439 -0.22742 0.46809 0.077108 0.88605 -0.039833 0.17362 -0.46127 -0.27142 -0.028306 -0.29867 0.29864 0.38303 0.30844 -0.3452 -0.51587 0.53314 0.65552 -0.71269 0.12006 0.65736 0.1268 0.063452 0.52105 -0.32129 0.090839 0.5402 -0.42388 0.26172 -0.53186 -0.22092 0.31098 0.085498 -0.20849 -0.11783 -0.67225 -0.37562 -0.37395 -0.26561 0.15955 -0.23379 0.30698 -0.42957 -0.15949 -0.056251 -0.53037 -0.00027318 0.031779 -0.050076 0.19838 0.5263 -0.3932 -0.020582 -0.34664 0.050339 0.10474 -0.16695 0.14653 -0.27417 -0.079003 0.3255 0.25679 -0.28822 -0.080767 -0.14702 0.2045 -0.16829 -0.61336 -0.15957 -0.87894 -0.22239 -0.38896 -0.1537 0.89022 -0.01441 -0.14972 0.043244 -0.29412 -0.3647 0.23843 -0.4078 0.12437 -0.74554 -1.0462 -0.0049372 0.064622 0.65929 0.16978 -0.30101 0.055172 -0.61906 0.12633 0.34866 0.80253 0.45186 -0.34535 0.027789 0.3698 -0.29596 0.090924 -0.38288 0.62553 -0.25017 0.54494 0.38816 -0.39468 -0.12659 -0.89322 -0.11581 0.058802 0.028621 -3.2418 0.50341 -0.38261 0.2517 0.04898 -0.14458 -0.042425 -0.14324 -0.11036 0.084105 0.50988 0.11401 -0.0090763 0.13166 -0.24277 0.11371 -0.013684 -0.16933 -0.31037 0.30032 -0.50483 -0.35793 0.19593 -0.19574 0.18822 -0.51626 -0.30086 0.025122 0.33054 -0.34896 0.016614 -0.23097 -0.071515 0.022549 0.47225 -0.52005 -0.039261 0.11731 0.047901 -0.35935 0.4679 0.79507 0.43192 0.63409 0.25967 -0.026803 0.48363 -0.034665 0.41269 -0.032735 0.078817 0.39341 0.011257 -0.060259 -0.24589 -0.13792 -0.62249 0.52543 -0.20884 0.07075 -0.21767 0.30439 -0.063656 0.2245 0.29913 0.60883 0.18624 -0.3114 -0.42916 -0.044703 0.2011 0.3446 0.16668 -0.12319 -0.011501 -0.40236"))
    em.persist(new WordVector("road", "0.19657 -0.13355 0.10811 0.16723 -0.61547 -0.11105 -2.1781 -0.099502 -0.047654 0.16427 -0.01546 -0.46114 -0.050751 0.22935 0.17734 0.12793 -0.064521 0.35632 0.062639 0.09152 -0.17468 0.02813 0.37049 0.28976 -0.54773 -0.31376 0.33071 0.61746 0.3353 0.51915 -0.096227 -0.41288 -0.31858 -0.20682 -0.51705 0.22036 0.42269 -0.20148 -0.67036 -0.35506 -0.090163 -0.10205 0.033472 0.01745 -0.25158 -0.072844 -0.17061 -0.72007 0.47719 -0.4104 0.1639 0.39847 0.4737 0.48863 0.34702 -0.056561 -0.16537 -0.33104 -0.42593 0.16563 0.51003 -0.036073 -0.10851 0.22855 -0.17544 -0.49146 0.23462 -0.037186 0.15336 -0.081646 -0.64928 -0.40102 -0.4478 -0.64307 0.28595 -0.080179 -0.71308 -0.13396 -0.74502 -0.13901 -0.16851 -0.070719 -0.073692 0.17235 0.11809 -0.42197 0.36986 0.34561 0.25378 -0.058157 0.21912 0.48138 -0.1767 -0.11006 0.064672 -0.76193 -1.5698 0.11013 0.28565 -0.60054 -0.046706 0.27543 -0.28666 0.13964 0.15008 -0.61811 -0.22418 -0.12645 0.13932 -0.077356 0.30311 0.21474 0.6206 -0.043171 0.3714 -0.73895 0.14749 0.25263 0.16933 -0.57411 0.041541 0.15709 -0.32377 0.13969 0.511 -0.57567 0.074125 0.44675 0.39382 -0.54125 0.8872 0.078979 -0.29657 0.36557 0.05544 -0.026977 -0.37003 0.059338 -0.56614 0.5927 -0.14454 -0.34338 -0.52441 0.028045 -0.0030574 0.11124 -0.091684 -0.56842 -0.013645 0.50818 -0.041561 -0.59017 0.46292 0.16556 -0.32247 -0.10967 0.21817 0.042397 0.61462 0.1043 0.76133 -0.31381 0.3583 -0.065212 0.13751 0.10952 0.050384 -0.30703 0.16633 -0.39121 0.46514 0.31807 -0.77591 0.10933 -0.37399 -0.22767 -0.1933 -0.10533 0.35255 -0.037728 -0.40834 0.28127 0.031301 -0.0088158 0.10109 -0.095706 0.094639 0.16393 -0.16661 0.15188 0.41826 -0.71922 -0.26603 0.53947 0.51603 0.31262 0.51981 0.4093 -0.77421 0.43917 -0.44793 -0.16181 -0.23733 0.3155 -0.091591 -0.14487 -0.60292 0.14835 0.058703 -0.54024 0.63889 0.29361 0.49561 -0.16593 0.44481 0.37646 0.18092 0.72922 -0.054273 0.38317 -0.56324 0.51847 0.14191 -0.17392 -3.9689 0.21981 -0.14954 -0.56891 0.25352 -0.70645 -0.36072 0.6492 0.011058 -0.59059 -0.22265 0.29432 0.8132 0.15429 -0.38722 -0.53562 0.039604 0.42941 -0.31022 0.21994 -0.15204 -0.23209 0.23931 -1.2437 -0.88975 -0.028109 0.18417 -0.34176 -0.17311 0.11285 0.11923 -0.35528 0.19257 -0.20329 -0.073164 0.71352 -0.16619 -0.23813 -0.13385 0.22185 0.63142 0.71721 0.059674 -0.28995 0.70922 -0.014343 0.095527 0.38856 -0.071236 -0.7354 -0.24534 0.44127 -0.31291 0.39344 0.52289 -0.21641 0.46274 -0.44055 -0.47603 0.71147 0.45479 0.01932 -0.22253 -0.21823 0.013281 0.37474 -0.080313 -0.064361 -0.75071 -0.043652 0.21664 0.42789 0.15336 -0.11078 -0.13985 -0.56231"))


    em.getTransaction.commit()
  }

  @Test
  def testSimilar: Unit = {
    val similar = new SimilarWord(0.5, new DbWordSource())
    assertEquals(0.6651, similar.similarity("illinois", "chicago"), 0.0001)
    assertEquals(0.5726, similar.similarity("road", "avenue"), 0.0001)
  }
}
