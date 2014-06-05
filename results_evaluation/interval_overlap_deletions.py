#! /usr/bin/env python

import sys
import math

class Point:
  def __init__(self, line):
    s=line.split()
    self.chr=s[0]
    self.sta=int(s[1])
    self.end=int(s[2])
    self.len=self.end-self.sta+1
  def dist(self,p2):
    return math.fabs(self.sta-p2.sta) + math.fabs(self.end-p2.end) 
  def recall(self, p2):
    if p2.sta > self.end or p2.end < self.sta:
      return 0
    if p2.sta <= self.sta:
      overlap_start = self.sta
    else:
      overlap_start = p2.sta 
    if p2.end >= self.end:
      overlap_end = self.end
    else :
      overlap_end = p2.end
    overlapped_bases = overlap_end - overlap_start + 1
    rec = float(overlapped_bases) / self.len
    return rec
  def precision(self, p2):
    return p2.recall(self)
  def f_score(self,p2):
    rec = self.recall(p2)
    pre = self.precision(p2)
    if rec == 0 and pre == 0 :
      return 0
    return 2*rec*pre/(rec+pre)

i1=0

if len(sys.argv) < 3 :
  print "Usage: <Insertion File> <SV Calls>"
  sys.exit(0)

f=open(sys.argv[1])
l1=f.readlines()
f=open(sys.argv[2])
l2=f.readlines()
f.close()


while i1<len(l1) :
  i2=0
  p2=Point(l2[i2])
  p1=Point(l1[i1])
  max_fscore= p1.f_score(p2)
  max_call = i2
  #while p2.sta < p1.end and i2_tmp< len(l2):
  while i2 < len(l2):
    p2 = Point(l2[i2])
    if p2.chr == p1.chr:
      t = l2[i2].split()[3]
      r = p1.f_score(p2)
      if r > max_fscore:
        max_fscore = r
        max_call = i2
    i2 += 1
  p2 = Point(l2[max_call])
  precision = p1.precision(p2)
  recall = p1.recall(p2)
  print "%s\t%d\t%d" %(p1.chr,p1.sta, p1.end),
  print "\t%f\t%f\t%f\t" %(recall, precision, max_fscore),
  print l2[max_call],

  i1 += 1
