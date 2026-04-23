package com.awarelytics.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DriftEventDao_Impl implements DriftEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DriftEvent> __insertionAdapterOfDriftEvent;

  private final EntityDeletionOrUpdateAdapter<DriftEvent> __updateAdapterOfDriftEvent;

  private final SharedSQLiteStatement __preparedStmtOfDeleteEventsBefore;

  public DriftEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDriftEvent = new EntityInsertionAdapter<DriftEvent>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `drift_events` (`event_id`,`timestamp`,`ml_probability`,`user_reaction`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DriftEvent entity) {
        statement.bindLong(1, entity.getEvent_id());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindDouble(3, entity.getMl_probability());
        statement.bindString(4, entity.getUser_reaction());
      }
    };
    this.__updateAdapterOfDriftEvent = new EntityDeletionOrUpdateAdapter<DriftEvent>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `drift_events` SET `event_id` = ?,`timestamp` = ?,`ml_probability` = ?,`user_reaction` = ? WHERE `event_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DriftEvent entity) {
        statement.bindLong(1, entity.getEvent_id());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindDouble(3, entity.getMl_probability());
        statement.bindString(4, entity.getUser_reaction());
        statement.bindLong(5, entity.getEvent_id());
      }
    };
    this.__preparedStmtOfDeleteEventsBefore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM drift_events WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DriftEvent event, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDriftEvent.insert(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DriftEvent event, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDriftEvent.handle(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEventsBefore(final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteEventsBefore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteEventsBefore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DriftEvent>> getAllEvents() {
    final String _sql = "SELECT * FROM drift_events ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drift_events"}, new Callable<List<DriftEvent>>() {
      @Override
      @NonNull
      public List<DriftEvent> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "event_id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMlProbability = CursorUtil.getColumnIndexOrThrow(_cursor, "ml_probability");
          final int _cursorIndexOfUserReaction = CursorUtil.getColumnIndexOrThrow(_cursor, "user_reaction");
          final List<DriftEvent> _result = new ArrayList<DriftEvent>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DriftEvent _item;
            final long _tmpEvent_id;
            _tmpEvent_id = _cursor.getLong(_cursorIndexOfEventId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpMl_probability;
            _tmpMl_probability = _cursor.getFloat(_cursorIndexOfMlProbability);
            final String _tmpUser_reaction;
            _tmpUser_reaction = _cursor.getString(_cursorIndexOfUserReaction);
            _item = new DriftEvent(_tmpEvent_id,_tmpTimestamp,_tmpMl_probability,_tmpUser_reaction);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DriftEvent>> getEventsSince(final long since) {
    final String _sql = "SELECT * FROM drift_events WHERE timestamp >= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drift_events"}, new Callable<List<DriftEvent>>() {
      @Override
      @NonNull
      public List<DriftEvent> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "event_id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMlProbability = CursorUtil.getColumnIndexOrThrow(_cursor, "ml_probability");
          final int _cursorIndexOfUserReaction = CursorUtil.getColumnIndexOrThrow(_cursor, "user_reaction");
          final List<DriftEvent> _result = new ArrayList<DriftEvent>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DriftEvent _item;
            final long _tmpEvent_id;
            _tmpEvent_id = _cursor.getLong(_cursorIndexOfEventId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpMl_probability;
            _tmpMl_probability = _cursor.getFloat(_cursorIndexOfMlProbability);
            final String _tmpUser_reaction;
            _tmpUser_reaction = _cursor.getString(_cursorIndexOfUserReaction);
            _item = new DriftEvent(_tmpEvent_id,_tmpTimestamp,_tmpMl_probability,_tmpUser_reaction);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object countEventsInRange(final long start, final long end,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM drift_events WHERE timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object avgProbabilityInRange(final long start, final long end,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(ml_probability) FROM drift_events WHERE timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLastEvent(final Continuation<? super DriftEvent> $completion) {
    final String _sql = "SELECT * FROM drift_events ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DriftEvent>() {
      @Override
      @Nullable
      public DriftEvent call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "event_id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMlProbability = CursorUtil.getColumnIndexOrThrow(_cursor, "ml_probability");
          final int _cursorIndexOfUserReaction = CursorUtil.getColumnIndexOrThrow(_cursor, "user_reaction");
          final DriftEvent _result;
          if (_cursor.moveToFirst()) {
            final long _tmpEvent_id;
            _tmpEvent_id = _cursor.getLong(_cursorIndexOfEventId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpMl_probability;
            _tmpMl_probability = _cursor.getFloat(_cursorIndexOfMlProbability);
            final String _tmpUser_reaction;
            _tmpUser_reaction = _cursor.getString(_cursorIndexOfUserReaction);
            _result = new DriftEvent(_tmpEvent_id,_tmpTimestamp,_tmpMl_probability,_tmpUser_reaction);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countIgnoredSince(final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM drift_events WHERE user_reaction = 'IGNORED' AND timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getReactionBreakdown(final long start, final long end,
      final Continuation<? super List<ReactionCount>> $completion) {
    final String _sql = "SELECT user_reaction, COUNT(*) as count FROM drift_events WHERE timestamp BETWEEN ? AND ? GROUP BY user_reaction";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReactionCount>>() {
      @Override
      @NonNull
      public List<ReactionCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserReaction = 0;
          final int _cursorIndexOfCount = 1;
          final List<ReactionCount> _result = new ArrayList<ReactionCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReactionCount _item;
            final String _tmpUser_reaction;
            _tmpUser_reaction = _cursor.getString(_cursorIndexOfUserReaction);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new ReactionCount(_tmpUser_reaction,_tmpCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
