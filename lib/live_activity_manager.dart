import 'dart:developer';

import 'package:flutter/services.dart';

class LiveActivityManager {
  final String channelKey;
  late final MethodChannel _methodChannel;

  LiveActivityManager({required this.channelKey}) {
    _methodChannel = MethodChannel(channelKey);
  }

  Future<void> checkLayoutPermission() async {
    try {
      await _methodChannel.invokeListMethod('checkLayoutPermission');
    } catch (e, st) {
      log(e.toString(), stackTrace: st);
    }
  }

  Future<void> startLiveActivity({required Map<String, dynamic> jsonData}) async {
    try {
      await _methodChannel.invokeListMethod('startLiveActivity', jsonData);
    } catch (e, st) {
      log(e.toString(), stackTrace: st);
    }
  }

  Future<void> updateLiveActivity({required Map<String, dynamic> jsonData}) async {
    try {
      await _methodChannel.invokeListMethod('updateLiveActivity', jsonData);
    } catch (e, st) {
      log(e.toString(), stackTrace: st);
    }
  }

  Future<void> stopLiveActivity() async {
    try {
      await _methodChannel.invokeListMethod('stopLiveActivity');
    } catch (e, st) {
      log(e.toString(), stackTrace: st);
    }
  }
}