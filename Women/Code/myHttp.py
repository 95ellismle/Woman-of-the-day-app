#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jul 29 19:52:43 2019

@author: oem
"""

import requests as rq
import bs4
import os
import time
import numpy as np


def __request_get(url, do_pause=True):
    """
    Will just get a webpage and return the requests object
    """
    if do_pause: time.sleep(abs(np.random.normal(0.5, 0.5)))
    
    headers = {'user-agent': 'My Scraper, Name: Matt Ellis Contact: 95ellismle@gmail.com'}
    r = rq.get(url, headers)
    print("Making Request at: %s" % url)
    if r.status_code < 300:
        return r
    else:
        print("Failed request for:\n\t%s" % url)
        return False


def get_page_soup(url, filename=False, do_pause=True, do_soup=True):
    """
    Will return the soup from a webpage.
    If unsucessful then will return False
    """
    if filename == False:
        r = __request_get(url, do_pause)
        if r is False: return False
    else:
        soup = open_html_page(filename, do_soup=do_soup)
        if soup is False:
            r = __request_get(url, do_pause)
            if r is False: return False
            save_html_page(r, filename)
        else:
            return soup
    if do_soup:
        return bs4.BeautifulSoup(r.text, features="lxml")
    return r



def save_html_page(obj, filename):
    """
    Will save a html page in the location specified. If the object isn't
    compatible then will return False.
    """
    filename = os.path.abspath(filename)
    
    if type(obj) == bs4.BeautifulSoup:
        text = str(obj)
    else:
        try:
            text = obj.text
        except:
            print("Obj = ", obj)
            print("type(obj) = ", type(obj))
            print("Can't save obj as it is not the right type")
            print("It should be a BeautifulSoup or Requests.get object")
            return False
    
    # Check if the folder exists
    folders = filename[:filename.rfind('/')]
    if not os.path.isdir(folders):
        os.makedirs(folders)

    # Create the file
    with open(filename, 'w') as f:
        f.write(text)


def open_html_page(filename, do_soup=True):
    """
    Will open an html file and return a soup object.
    """
    if not os.path.isfile(filename):
        print("Cannot find file:\n\t%s" % filename)
        return False

    with open(filename, 'r') as f:
        txt = f.read()
    if do_soup:
        soup = bs4.BeautifulSoup(txt, "lxml")
        return soup
    else:
        return txt
